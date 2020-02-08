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
    [rs.style :as css]
    [garden.color :as color :refer [hsl rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [percent px pt em ms]]
    [garden.types :as gt]
    [rs.css :as rcss :refer [fr rad deg %]]
    [clojure.string :as string]))

; this is the entire state of the application
; note the use of a Reagent atom when this runs
; as Clojurescript or a normal Clojure atom if this
; is running in Clojure

(defonce app-state
  #?(:cljs (ra/atom nil) :clj (atom nil)))

(defn change-thing
  ([state {value :value {tv :value} :target path :path :as msg}]
    (assoc-in state path (or value tv))))

(defn change-text
  ([state {value :value {tv :value} :target path :path :as msg}]
    (assoc-in state (conj path :value) (or value tv))))

(defn ndom
  ([i x]
   (println (apply str (concat (repeat i "  ") [x])))
    (if (or (list? x) (and (vector? x) (not (keyword? (first x)))))
      (map (partial ndom (inc i)) x)
      (let [[t c] x]
        (case [t (empty? c)]
          [:#text false] [:div c]
          [:div false]   [:div (ndom (inc i) c)]
          [:div true]    [:div]
          [:br true]     [:div]
          x)))))

(defn debug-text
  ([state {dom :dom-tree
           selection :selection :as msg}]
   (println "-" selection)
   (println ">*" (ef/at [:div] (ef/div "qwe")))
   state))

(defn change-dom-tree
  ([state {dom :dom-tree path :path :as msg}]
    (assoc-in state (conj path :value) (ndom 0 dom))))

(declare initialize-state)

(defn make-event-map [state]
  {
    {:click :reinitialize}    initialize-state
    {:change :thing}          change-thing
    {:change :text}           change-text
    {:debug :text}            debug-text
    {:change :dom-tree}       debug-text
  })

(defn reset-css [state]
  (assoc state :css (css/css-rules {})))

(defn make-state
  ([]
   (make-state
     {
      :params
      {
       :text {:value "this and that" :kind :text :name "Message"}
       :number {:unit :double :min -16 :max 16 :magnitude 5 :step 0.1 :name "a number"}
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
  [{event-map :event-map :as state} {match :match message :message}]
  ((get event-map match change-thing) state message))

(defn handle-message!
  "Maybe updates the app state with
  a function that depends on the given message"
  ([message]
    (swap! app-state
      (fn [current-state]
        (handle-message current-state message)))))
