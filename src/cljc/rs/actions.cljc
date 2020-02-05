(ns rs.actions
  "
    This namespace has functions
    that change the state of the application

    Each function that changes the app state
    takes the current state app-state and a message,
    and returns a new state

    This namespace can be tested in a normal Clojure REPL too
  "
  (:require
    [garden.core :as gc]
    #?(:cljs [reagent.ratom :as ra])
    #?(:cljs [oops.core :refer [oget ocall]])
    [rs.style :as css]
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

(defn change-thing
  ([state {value :value {tv :value} :target path :path :as msg}]
   (println "change >" msg)
    (assoc-in state path (or value tv))))

(declare initialize-state)

(defn make-event-map [state]
  {
    {:click :reinitialize} initialize-state
    {:change :colour-swatch} change-thing
  })

(defn reset-css [state]
  (assoc state :css (css/css-rules {})))

(defn make-state
  ([]
   (make-state
     {
      :params
       {
         :colour (rgb 255 0 0)
         :text "hi there"
         :number {:min -1 :max 16 :value 5 :step 0.1}
       }
     }))
    ([state]
      (-> state
        (assoc :event-map (make-event-map state))
        (assoc :css (css/css-rules {})))))

(defn initialize-state
  ([]
    (initialize-state {} {}))
  ([state message]
   (-> (make-state)
     (assoc :event-map (make-event-map state)))))

(defn handle-message
  "Returns a new state from the given state and message"
  [{event-map :event-map :as state} {params :params :as message}]
   (println ">" message)
  ((get event-map (dissoc message :params) change-thing) state params))

(defn handle-message!
  "Maybe updates the app state with
  a function that depends on the given message"
  ([message]
    (swap! app-state
      (fn [current-state]
        (handle-message current-state message)))))
