(ns rs.views
  "
   This namespace has functions
   that make view components for the UI
  "
  (:require
    [oops.core :refer [oget]]
    [cognitect.transit :as t]
    [garden.core :as gc :refer [css]]
    [garden.color :as color :refer [hsl rgb hsla rgba hex->rgb as-hex]]
    [garden.units :as u :refer [px pt em ms percent defunit]]
    [garden.types :as gt]
    [garden.selectors :as gs]
    [rs.css :as rcss :refer [parse-value fr rad deg rotate3d perspective translate strs sassify-rule named?]]
    [rs.actions :as actions]
    [clojure.string :as string]
    [rs.css :as css]))


(defn css-view
 "
   Returns a CSS component
   from the given list of rules
   and optional flags
 "
 ([rules]
   (css-view nil {} rules))
 ([flags rules]
   (css-view nil flags rules))
 ([id flags rules]
   [:style (if id {:type "text/css" :id id} {:type "text/css"})
    (css flags
      (mapcat
        (fn [[f & _ :as l]]
          (if (or (string? f) (named? f))
            (map sassify-rule (partition 2 l)) l))
(if (map? rules) rules (partition-by :identifier rules))))]))


(defn input-text-view
  "
    Returns a textarea component
    that changes the :text key of the state
  "
  [{v :value title :title path :path id :id}]
  [:input.input.text-input
   {
    :type  :textarea
    :id    id
    :title title
    :value v
    :on-change
           (fn [e] (actions/handle-message! {:path path :value (oget e [:target :value])}))
    }])

(defn input-number-view
  "Returns an input view that converts to/from a number"
  [{v :value min :min max :max step :step title :title path :path}]
  [:input.input
   {
    :type  :range
    :title title
    :min   min
    :max   max
    :step  step
    :value v
    :on-change
           (fn [e]
             (actions/handle-message! {:path path :value (js/parseFloat (oget e [:target :value]))}))
    }])

(defn input-unit-view
  "Returns an input view that converts to/from em units"
  [{u :unit v :value min :min max :max step :step title :title path :path}]
  [:input.input
   {
    :type  :range
    :title (or title (str path))
    :min   min
    :max   max
    :step  step
    :value (get v :magnitude)
    :on-change
           (fn [e]
             (actions/handle-message! {:path path :value (u (js/parseFloat (oget e [:target :value])))}))
    }])


(defn text-demo
  "takes the text from the text-input function and displays it in a component"
  [{path  :path
    id    :id
    value :value
    }]
  [:div {:id id} value]
  )

(defn unit-view [{:keys [unit magnitude]}]
  [:div {:title (name unit) :class (str "unit " (if (= "%" (name unit)) "percent" (name unit)))} (str magnitude)])

(defn listy-view
  "Returns a view to display a list of things"
  [a-list]
  (into [:div.list]

        (map
          (fn [v]
            [:div
             (cond
               (:magnitude v) [unit-view v]
               (:hue v) (str "(hsl " (string/join " " [(:hue v) (:saturation v) (:lightness v)]) ")")
               (or (list? v) (vector? v)) [listy-view v]
               :otherwise (str v))])
          a-list)))

(defn formatter [a-string]
  (string/capitalize (string/join " " (string/split a-string #"-"))))

(defn sliders-view [parameter-maps]
  (into [:div]
        (mapcat
          (fn [{unit :unit :as parameter-map}]
            [
             [:div.title (formatter (name (last (filter keyword? (:path parameter-map)))))]
             (if unit
               [input-unit-view parameter-map]
               [input-number-view parameter-map]
               )
             ])
          parameter-maps)))

;(defn colours [canvas-colours button-colours]
;  ([canvas-colours :hue :lightness]
;    [{
;      :hue       hue
;      :lightness lightness
;
;      }
;     (+ hue 180 )
;     (+ lightness 30)])
;
;  )

(defn table-view
  "Returns a view to display a table
   of the given map's key-value pairs"
  [a-map]
  (into [:div.table {:title (str a-map)}]
    (mapcat
      (fn [[k v]]
       [[:div (str (if (keyword? k) (name k) k))]
        (cond
          (:magnitude v) [unit-view v]
          (or (:red v) (:hue v))       [:div.colour {:style {:background (css/css-str v)}}]
          (or (list? v) (vector? v)) [:div [listy-view v]]
          :otherwise [:div (str v)])
        ])
      a-map)))

(defn root-view
  "
   Returns a view component for
   the root of the whole UI

   We only pass the data each view needs

   Each component has its own CSS where possible
  "
  ([] (root-view @actions/app-state))
  ([{
     slider-parameters        :slider-parameters
     slider-button-parameters :slider-button-parameters
     {main-rules      :main-rules
      canvas-rules    :canvas-rules
      animation-rules :animation-rules imported :imported} :css
                              {t :text} :input-text
                              :as state}]
   [:div.root
    [css-view :main-rules {:vendors ["webkit" "moz"] :auto-prefix #{:column-width :user-select}} main-rules]
    [css-view :animation-rules {} animation-rules]
    [:div.main
     [:div.button {:title "reinitialize everything!" :on-click (fn [e] (actions/handle-message! {:clicked :reinitialize}))} "🔄"]
     #_(into [:div.imported-rules]
      (map (fn [[k v]] [:div.imported-rule [:div.imported-rule.selector (str k)] [:div.imported-rule.rule (str v)]]) (:base imported)))
     (into [:div.colours]
      (map (fn [c] [:div.colour {:style {:background (css/css-str c)}}])
        (into #{} (mapcat (fn [[k v]] [(get v "color")]) (:base imported)))))
     [:div.things
      [:div.canvas-parameters "Canvas settings"
       [css-view :canvas-rules {:vendors ["webkit" "moz" "o" "ms"] :auto-prefix #{:appearance}} canvas-rules]
       [sliders-view (map (fn [{path :path :as parameter}] (assoc parameter :value (get-in state path))) slider-parameters)]
       ]
      [:div {:id "canvas"}
       [:div {:id "button-wrapper"}
        [:div {:id "demo-button"} [text-demo {:id "text-demo" :path [:input-text :text] :value t}]]]]
      [:div.button-parameters "Button settings"
       [sliders-view (map (fn [{path :path :as parameter}] (assoc parameter :value (get-in state path))) slider-button-parameters)]
       ]
      [input-text-view {:id "input-text" :path [:input-text :text] :value t}]
      ]
     ]]))

(defn boot-view
  "

  "
  ([] (boot-view @actions/app-state))
  ([{
     slider-parameters        :slider-parameters
     slider-button-parameters :slider-button-parameters
     {main-rules      :main-rules
      canvas-rules    :canvas-rules
      units-rules     :units
      animation-rules :animation-rules imported :imported} :css
                              {t :text} :input-text
                              :as state}]
   [:div.root
    [css-view :main-rules {:vendors ["webkit" "moz"] :auto-prefix #{:column-width :user-select}} main-rules]
    [css-view :animation-rules {} animation-rules]
    [css-view :units-rules {} units-rules]
    [:div.main
     (into [:div.colours]
      (map (fn [c] [:div.colour {:style {:background (css/css-str c)}}])
        (into #{} (mapcat (fn [[k v]] [(get v "color")]) (mapcat val imported)))))
     (into [:div.imported-rules]
      (map
       (fn [[k v]] [:div.imported-rule [:div.imported-rule.selector (str k)]
                  [:div.imported-rule.rule [table-view v]]])
        (mapcat val imported)))
     ]]))


(defn to-clj [s]
  (reduce
    (fn [r i]
      (assoc r (.item s i) (parse-value (.item s i) (.getPropertyValue s (.item s i)))))
    {} (range (.-length s))))

(defn to-transit [state]
  (t/write (t/writer :json-verbose) state))

(defn download!
  ([tipe naym contents]
    (println "download  as" tipe naym)
    (let [
          a (.createElement js/document "a")
          f (js/Blob. (clj->js [contents]) {:type (name tipe)})
          ]
      (set! (.-href a) (.createObjectURL js/URL f))
      (set! (.-download a) (str naym "." (name tipe)))
      (println "<a>" a)
      (.dispatchEvent a (js/MouseEvent. "click")))))

(defn extract [i naym]
  (fn [e]
    (println "loaded" i naym)
    (let [s (.-cssRules (aget (.. js/document -styleSheets) i))
         sr (reduce
              (fn [r [selector style]]
                (assoc r selector (to-clj style)))
              {} (remove (fn [[k v]] (or (nil? k) (nil? v))) (map (fn [i] [(.-selectorText (.item s i)) (.-style (.item s i))]) (range (.-length s)))))]
     ;(doseq [[k v] sr] (println k "   " v))
     (download! "edn" naym (str sr)))))

(defn extraction-view
  "
    A view that converts the given
    local CSS files into .edn files
  "
  ([]
    (extraction-view
      [
        "base.css"
        "addons.css"
        "docs.css"
        "responsive.css"
        "styles.css"
        "prettify.css"
      ]))
  ([urls]
   [:div
    (into [:div]
      (map-indexed
        (fn [i n]
          [:link {:type "text/css" :rel "stylesheet" :href (str "css/" n) :on-load (extract i n)}])
        urls))
     [:div "hi there"]]))
