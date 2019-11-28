(ns rs.actions
  "
    This namespace has functions
    that change the state of the application

    Each function that changes the app state
    takes the current state app-state and a message map,
    and returns a new state

    This namespace can be tested in a normal Clojure REPL too
  "
  (:require
    [garden.core :as gc]
    #?(:cljs [reagent.ratom :as ra])
    #?(:cljs [oops.core :refer [oget ocall]])
    #?(:cljs [rs.messaging :as msgn])
    [rs.style :as css]
    [ajax.core :refer [GET POST]]
    [garden.color :as color :refer [hsl rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [percent px pt em ms]]
    [garden.types :as gt]
    [clojure.string :as string]))

; this is the entire state of the application
; note the use of a Reagent atom when this runs
; as Clojurescript or a normal Clojure atom if this
; is running in Clojure
(defonce app-state
         #?(:cljs (ra/atom nil) :clj (atom nil)))

(declare handle-message!)

(def readers
  {
   'garden.types.CSSUnit gt/map->CSSUnit
   'garden.color.CSSColor color/map->CSSColor
   })

(defn fold-rules [state]
  (reduce
    (fn [r [rk rv]]
      (assoc-in r [:css :display rk] {:self :unfolded}))
    state (get-in state [:css :imported])))

(defn got-edn!
  [state {k :key edn :edn}]
  (fold-rules
   (assoc-in state [:css :imported k]
     #?(:cljs (cljs.reader/read-string {:readers readers} edn)
        :clj (read-string {:readers readers} edn)))))

(defn get-edn!
  ([state]
    (doseq [k [:addons :base :docs :prettify :responsive :styles]]
      (GET (str "/style/" (name k) ".css.edn")
        {:handler (fn [edn] (handle-message! {:got :edn :params {:key k :edn edn}}))}))
    state))

(defn colour-coupling
  "makes sure the background color of the canvas is always complimentary to the background colour of the button"
  [hue lightness]
  [(mod (+ 180 hue) 360) (mod (+ 30 lightness) 100)])

(defn update-colours
  [
   {
    {{
      {{h :hue l :lightness} :background} "#canvas"
      button                              "#demo-button"
      } :canvas-rules} :css
    :as state
    }
  ]
  (update-in state [:css :canvas-rules "#demo-button" :background]
    (fn [colour]
      (let [[nh nl] (colour-coupling h l)]
        (assoc colour :hue nh :lightness nl)))))

(defn change-thing
  ([state {value :value {tv :value} :target path :path}]
   ;(println "change>" path value)
    (assoc-in state path (or value tv))))

(defn update-css
  ([state {value :value path :path}]
   ;(println "css> " path value)
    (update-in state path
      (fn [rules]
         (println "css>  " path rules)
        (merge value rules)))))

(defn toggle-rule
  ([state {value :value path :path d :display}]
    (update-in state path
      (fn [s]
        (if (nil? s)
        :unfolded nil)))))

(declare initialize-state)

(defn make-event-map [state]
  {
    {:click :rule} toggle-rule
    {:click :reinitialize} initialize-state
    {:change :colour-swatch} change-thing
    {:update :css} update-css
    {:got :edn} got-edn!
  })

(defn make-state
  ([]
   (make-state
     {
      :view-state {[:css :imported]  :hidden}
      ;These are the parameters of the canvas that the sliders manipulate: they take canvas rules, and pick out the parameters
      :slider-parameters
      [
       {:unit px :min 0 :max 10 :step 1 :path [:css :canvas-rules "#canvas" :border 0 0]}
       {:min 0 :max 360 :step 1 :path
        [:css :canvas-rules "#canvas" :background :hue]}
       {:unit em :min 0 :max 5 :step 0.2 :path
        [:css :canvas-rules "#canvas" :border-radius]}

       ;{:unit px :min 0 :max 50 :step 0.5 :path
       ;       [:canvas-rules "#demo-button" :box-shadow ::blur]}
       ]
      :slider-button-parameters
      [
       {:min 0 :max 360 :step 1 :path
        [:css :canvas-rules "#demo-button" :background :hue]}
       {:unit px :min 0 :max 50 :step 0.5 :path
        [:css :canvas-rules "#demo-button" :border-radius]}
       {:unit pt :min 11 :max 20 :step 0.5 :path
        [:css :canvas-rules "#demo-button" :font-size]}
       ]

      :input-text
      {:text "Button text"}

      }))
    ([state]
      (-> state
        (assoc :event-map (make-event-map state))
        (assoc-in [:css] (css/css-rules {}))
        get-edn!)))

(defn subscribe-chat!
  [{{:keys [write-channel]} :messaging :as state}]
  #?(:cljs
      (msgn/subscribe! state :server-event
       (fn [message]
         ;(println "<" t e (dissoc e :say :topic :type :uuid))
         (handle-message! (dissoc message :topic))))
     :clj state))

(defn initialize-state
  ([]
    (initialize-state {} {}))
  ([state message]
   (-> (make-state)
     (assoc :event-map (make-event-map state))
     #?(:cljs (msgn/add-messaging! subscribe-chat! nil)
        :clj identity))))

(defn handle-message
  "Returns a new state from the given state and message"
  [{event-map :event-map :as state} {params :params :as message}]
  ((get event-map (dissoc message :params) change-thing) state params))

(defn handle-message!
  "Maybe updates the app state with
  a function that depends on the given message"
  ([message]
    (swap! app-state
      (fn [current-state]
        (handle-message current-state message)))))
