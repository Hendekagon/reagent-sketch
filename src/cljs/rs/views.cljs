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

(defn on [{{c :convert :or {c identity} prevent-default? :prevent-default?} :params :as message}]
  (fn [e]
    (let [v (try (oget e [:target :value])
              (catch js/Error error nil))]
      (actions/handle-message!
       (assoc-in message [:params :target :value] (c v))))))

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

(defn unit-view [{{:keys [unit magnitude]} :value path :path}]
  [:div {:title (name unit) :class (str "unit " (if (= "%" (name unit)) "percent" (name unit)))}
    (str magnitude (name unit))])

(defn colour-view [{colour :value path :path}]
  [:input {:type :color :value (css/css-str colour)
           :on-input (on {:change :colour-swatch :params {:path path :convert hex->rgb}})
           :on-change (on {:change :colour-swatch :params {:path path :convert hex->rgb}})
           }])

(defn listy-view
  "Returns a view to display a list of things"
  [{a-list :value path :path}]
  (into [:div.list]
        (map-indexed
          (fn [i v]
            [:div.list-item
             (cond
               (:magnitude v) [unit-view {:value v :path (conj path i)}]
               (or (:red v) (:hue v)) [colour-view {:colour v :path (conj path i)}]
               (or (list? v) (vector? v)) [listy-view {:value v :path (conj path i)}]
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

(defn table-view
  "Returns a view to display a table
   of the given map's key-value pairs"
  [{a-map :value path :path}]
  (into [:div.table {:title (str a-map)}]
    (mapcat
      (fn [[k v]]
       [[:div (str (if (keyword? k) (name k) k))]
        (cond
          (:magnitude v) [unit-view {:value v :path (conj path k)}]
          (or (:red v) (:hue v)) [colour-view {:value v :path (conj path k)}]
          (or (seqable? v) (list? v) (vector? v)) [listy-view {:value v :path (conj path k)}]
          :otherwise [:div (str v)])
        ])
      (sort-by key a-map))))


(defn animation-rules []
  [
   (gt/->CSSAtRule :keyframes
                   {:identifier :rotation
                    :frames
                      [
                       [:0% {:transform (translate (percent 0) (percent 0))}]
                       [:25% {:transform (translate (percent 0) (percent -25))}]
                       [:50% {:transform (translate (percent -25) (percent 0))}]
                       [:75% {:transform (translate (percent 0) (percent 25))}]
                       [:100% {:transform (translate (percent 0) (percent 0))}]
                       ]
                    })
   ["#card1"
    {
     :background                (hsla 168 100 80 1)
     :height                    (px 200)
     :width                     (px 200)
     :display                   "table"
     :margin                    "auto"
     :animation-name            :rotation
     :animation-duration        (ms 10000)
     :animation-iteration-count :infinite
     :animation-timing-function :linear
     }]])


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
      animation-rules :animation-rules
      imported :imported} :css
      {t :text} :input-text
      :as state}]
   [:div.root
    [css-view :main-rules {:vendors ["webkit" "moz"] :auto-prefix #{:column-width :user-select}} main-rules]
    [css-view :animation-rules {} (animation-rules)]
    [:div.main
      [:div#card1 "hi!"]
     ]]))

(defn rule-view [{:keys [value display-path selector display] :as rule}]
  [:div.imported-rule
    [:div.imported-rule.selector
     {:on-click (on {:click :rule :params {:path display-path :value value :with display}})
     :title (str selector)}]
     [:div {:class (str "imported-rule.rule." display)}
      (when (not (nil? display))
        [table-view rule])]])

(defn boot-view
  "

  "
  ([] (boot-view @actions/app-state))
  ([{
     slider-parameters        :slider-parameters
     slider-button-parameters :slider-button-parameters
     {main-rules      :main-rules
      canvas-rules    :canvas-rules
      units-rules     :units-rules
      display         :display
      animation-rules :animation-rules
      imported :imported} :css
                              {t :text} :input-text
                              :as state}]
   [:div.root
    [css-view :main-rules {:vendors ["webkit" "moz"] :auto-prefix #{:column-width :user-select}} main-rules]
    [css-view :animation-rules {} animation-rules]
    [css-view :units-rules {} units-rules]
    [:div.main
     [:div.colour-previews
      [:div "Unique Colours"]
      (into [:div.colours]
        (map (fn [c] [:div.colour-swatch {:style {:background (css/css-str c)}}])
          (into #{} (mapcat (fn [[k v]] [(get v "color")]) (mapcat val imported)))))]
     #_[:div.unique-properties [:div "Unique properties"]
     [table-view {:value (into {} (map (juxt key (comp (partial map last) val)) (group-by first (distinct (mapcat last (mapcat val imported))))))}]]
     (into [:div.imported-rules]
       (map
         (fn [[ik im]]
           (if (= :folded (get-in display [ik :self]))
             [:div.imported-ruleset-box
               [:div.ruleset-title (name ik)]]
             [:div.imported-ruleset-box [:div.ruleset-title (name ik)]
              (into [:div.imported-ruleset]
                (map
                 (fn [[k v]]
                   [rule-view {:value v :selector k :path [:css :imported ik k]
                               :display-path [:css :display ik k] :display (get-in display [ik k])}])
                  (sort-by (comp count val) im)))]))
         imported))
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
