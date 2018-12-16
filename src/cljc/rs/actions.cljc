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
    [rs.css :as css :refer [fr strs]]
    [garden.color :as color :refer [hsl rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [percent px pt em ms]]
    [clojure.string :as string]))

; this is the entire state of the application
; note the use of a Reagent atom when this runs
; as Clojurescript or a normal Clojure atom if this
; is running in Clojure
(defonce app-state
         #?(:cljs (ra/atom nil) :clj (atom nil)))


(defn make-state
  ([]
   (make-state
     {

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
        css/add-main-rules
        css/add-canvas-rules
        css/add-units-rules
        css/add-grid-rules
        css/add-animation-rules)))


(defn initialize-state
  ([state message]
   (make-state)))

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
  ([state {value :value path :path :as message}]
   (if (= path [:canvas-rules "#canvas" :background :hue])
     (update-colours
      (assoc-in state path value))
      (assoc-in state path value))))

(defn choose-function
  "Work out what function to use for updating
  state based on the given message"
  ([{of-what-was-clicked-on :clicked :as msg}]
   (case of-what-was-clicked-on
     :reinitialize initialize-state
     change-thing)))

(defn handle-message
  "Returns a new state from the given state and message"
  [state message]
  ((choose-function message) state message))

(defn handle-message!
  "Maybe updates the app state with
  a function that depends on the given message"
  ([message]
   (handle-message! message (choose-function message)))
  ([message a-function]
    (swap! app-state
      (fn [current-state] (a-function current-state message)))))
