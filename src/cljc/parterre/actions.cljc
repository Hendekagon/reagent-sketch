(ns parterre.actions
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
    [parterre.style :as css]
    [garden.color :as color :refer [hsl rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [percent px pt em ms]]
    [garden.types :as gt]
    [parterre.css :as rcss :refer [fr rad deg %]]
    [clojure.string :as string]))

; this is the entire state of the application
; note the use of a Reagent atom when this runs
; as Clojurescript or a normal Clojure atom if this
; is running in Clojure

(defonce app-state
  #?(:cljs (ra/atom nil) :clj (atom nil)))

(declare initialize-state)

(defn assoc-in*
  ([state {value :value {tv :value} :target path :path :as msg}]
    (assoc-in state path (or value tv))))

(defn reset-css [state]
  (assoc state :css (css/css-rules {})))

(defn make-event-map [state]
  {
    {:click :reinitialize}    initialize-state
    {:change :range-slider}   assoc-in*
  })

(defn make-state
  ([] (make-state {}))
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
  [{event-map :event-map :as state} {match :match message :message}]
  ((get event-map match assoc-in*) state message))

(defn handle-message!
  "Maybe updates the app state with
  a function that depends on the given message"
  ([message]
    (swap! app-state
      (fn [current-state]
        (handle-message current-state message)))))
