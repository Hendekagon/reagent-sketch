(ns ^:figwheel-no-load rs.style
  (:require
    [garden.color :as color :refer [hsl hsla rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pt em ms]]
    [garden.types :as gt]
    [garden.arithmetic :as a]
    [rs.css :refer [fr vw vh linear-gradient calc]]))

(defn main-rules [params]
  {
   :.root
   {
    :background (rgb 30 30 30)
    :width (percent 100)
    :height (em 20)
    :display :flex
    }
   :.main
   {
    :background (rgb 30 30 30)
    :color (hsl 0 0 100)
    :width (percent 100)
    :height (em 100)
    :display :flex
    :flex-flow '[[column nowrap]]
    :font-family ["Gill Sans" "Helvetica" "Sans Serif"]
    :font-weight :normal
    :font-size (em 1)
    }
   :.imported-rules {
                     :background :black
                     :color (rgb 240 240 240)
                     :width (percent 100)
                     :display :flex
                     :flex-flow '[[row wrap]]
                     :font-size (em 1)
                     }
   :.imported-ruleset-box {
                           :background :black
                           :border-color :red
                           :border-width (px 1)
                           :border-style :solid
                           :color (rgb 240 240 240)
                           :width (percent 100)
                           :display :grid
                           :grid-template-rows [[(em 4) (fr 1)]]
                           :grid-row-gap (em 0.1)
                           :grid-column-gap (em 2)
                           :justify-items :center
                           }
   :.ruleset-title {:grid-row 1 :display :flex :justify-content :center
                    :align-items :center :font-size (em 2)}
   :.imported-ruleset {
                       :background :black
                       :border-color :orange
                       :border-width (px 1)
                       :border-style :solid
                       :width (calc "100% - 4em")
                       :color (rgb 240 240 240)
                       :display :flex
                       :flex-flow '[[row wrap]]
                       :grid-row 2
                       }
   :.imported-rule {:display :flex :flex-flow '[[row wrap]] :margin (px 4)
                    :width (px 32) :height (px 32)}
   :.selector {:background (rgb 40 40 40) :display :flex :flex-flow "row wrap" :width (em 16) :margin 0 :padding (em 0.5)}
   :.rule {:display :flex :width (em 32) :background (rgb 50 50 50) :margin 0}
   :.colours {:display :flex :flex-flow "row wrap" :background (rgb 100 100 100) :margin (em 1)}
   :.colour-previews {:display :flex :flex-flow "row wrap" :padding (em 1)}
   :.colour {:display :flex :width (px 16) :height (px 16)}
   :.colour-swatch {:display :flex :min-width (px 24) :min-height (px 24) :max-width (px 64) :max-height (px 64)
                    :margin (px 8)}
   :.table
   {
    :padding (em 1)
    :display :grid
    :color :white
    :grid-template-columns [[(percent 70) (fr 1)]]
    :grid-auto-rows (em 1.5)
    :grid-row-gap (em 0.7)
    :grid-column-gap (em 2)
    }
   :.list {
           :display :flex
           :flex-flow [[:column :wrap]]
           :max-height (em 16)
           }
   :.unique-properties
   {
    :width (percent 100)
    }
   ".things"
   {
    :display :grid
    :grid-area :content
    :grid-template-columns [[(percent 20) (fr 1)]]
    :grid-column-gap (em 1)
    :grid-auto-rows :auto
    :grid-row-gap (em 2.5)
    :background (rgb 200 205 210)
    :padding (percent 4)
    }
   :body {
          :background (rgb 30 30 30)
          :font-family ["Gill Sans" "Helvetica" "Verdana" "Sans Serif"]
          :font-size (em 1)
          :font-weight :normal
          :cursor :default
          :padding 0
          :margin 0
          :width (percent 100)
          }
   :.button {
             :cursor :pointer
             :width (px 10)
             :height (px 28)
             :margin-left (percent 1)
             }
   ".padding_em-1" {:padding (em 1)}
   ".button-refresh:hover" {}
   "#text-demo" {
                 :color (hsl 20 30 90)

                 }
   :div.canvas-parameters {
                           :max-height (px 200)
                           :padding (px 10)
                           }
   :div.button-parameters {
                           :height (percent 50)
                           :padding (px 10)
                           }

   })


(defn units-rules [params]
  {:.unit {
           :justify-self :start
           :display :flex
           :justify-content :center
           :align-items :center
           :min-width (em 2)
           :max-width (em 5)
           :border-radius (px 4)
           :border :none
           :background (rgb 150 150 150)
           :padding (em 0.1)
           }
   :.em {:background (hsl 150 50 50) :color (hsl 150 20 10)}
   :.px {:background (hsl 30 30 70) :color (hsl 15 20 10)}
   :.percent {:background (hsl 50 70 80) :color (hsl 15 20 10)}
   :.fr {:background (hsl 200 70 80) :color (hsl 15 20 10)}})


(defn animation-rules [params]
  [
   (gt/->CSSAtRule :keyframes
     {:identifier :gradient-flow
      :frames [
               [:0% {:background (linear-gradient "to top" (rgb 100 100 100) (rgb 255 255 255))}]
               [:50% {:background (linear-gradient "to top" (rgb 255 255 255) (rgb 150 150 150))}]
               [:100% {:background (linear-gradient "to top" (rgb 100 100 100) (rgb 255 255 255))}]]})
   ])

(defn css-rules [params]
  (-> {}
    (assoc :main-rules  (main-rules params))
    (assoc :units-rules (units-rules params))
    (assoc :animation-rules (animation-rules params))))