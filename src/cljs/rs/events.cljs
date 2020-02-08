(ns rs.events
  (:require
    [rs.actions :as actions]
    [applied-science.js-interop :as j]
    [oops.core :refer [oget ocall]]
    [cljs-bean.core :refer [bean ->clj ->js]]
    [clojure.string :as string]))

(defn tree
   ([p n]
    (let [nodes (oget p [:childNodes]) n (oget nodes :length)
          h [(keyword (string/lower-case (oget p :nodeName))) (oget p :nodeValue)]]
      (if (> n 0)
        [h (mapv (fn [i] (let [p (ocall nodes :item i)] (tree p p))) (range n))]
        h)))
    ([e]
     (let [nodes (oget e [:target :childNodes]) n (oget nodes :length)]
       (if (> n 1)
         (mapv (fn [i] (let [p (ocall nodes :item i)] (tree p p))) (range n))
         (let [p (ocall nodes :item 0)] (tree p p))))))

(defn ce [match {convert :convert :or {convert identity}
                 prevent-default? :prevent-default?
                 :as message} e]
  (.log js/console (.getSelection js/window))
  (actions/handle-message!
    {:match match :message
     (-> message
       (assoc :selection (-> (.getSelection js/window)))
       (assoc :dom-tree (oget e :target)))}))

(defn on [match {convert :convert :or {convert identity}
                 prevent-default? :prevent-default?
                 :as message}]
  (fn [e]
     (if (= "true" (.. e -target -contentEditable))
       (ce match message e)
       (let [v (try (oget e [:target :value])
                 (catch js/Error error nil))]
         (actions/handle-message!
           {:match match :message (assoc-in message [:target :value] (convert v))})))))