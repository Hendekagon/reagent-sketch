(ns rs.css
  "CSS things - extra functions and definitions for CSS
  and some CSS rules"
  (:require
    [garden.color :as color :refer [hsl hsla rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pt em ms]]
    [garden.core :as gc :refer [css]]
    [garden.compiler :as gcomp]
    [garden.types :as gt]
    [garden.stylesheet :refer [at-media]]
    [rs.css.core :refer [fr rad deg rotate translate scale linear-gradient]]
    [clojure.string :as string]))

(defn add-canvas-rules [state]
  (-> state
      (assoc-in [:css :canvas-rules]
        {
         "#canvas"
                           {
                            :border        [[(px 0) :solid (rgb 86 86 86)]]
                            :background    (hsla 210 70 85 1)
                            :border-radius (em 0)
                            :width         (percent 90)
                            :height        (px 500)
                            :display       "table"
                            }
         "#button-wrapper" {
                            :display        "table-cell"
                            :vertical-align "middle"
                            }
         "#demo-button"    {
                            :background    (hsl 109 100 70)
                            :max-width     (px 100)
                            :height        (px 50)
                            :overflow      "hidden"
                            :padding       (em 0.5)
                            :display       :flex
                            :margin        "auto"
                            :border-radius (px 4)
                            :font-size     (pt 16)
                            :border-width  (px 1)
                            :justify-content :center
                            :align-items :center
                            }
         })))


(defn add-main-rules [state]
  (assoc-in state [:css :main-rules]
    {
     :.main
                             {
                              :background  (rgb 30 30 30)
                              :color       (hsl 0 0 100)
                              :width       (percent 100)
                              :height      (percent 100)
                              :display     :flex
                              :flex-flow   "column nowrap"
                              :font-family ["Gill Sans" "Helvetica" "Sans Serif"]
                              :font-weight :normal
                              :font-size   (em 1)
                              }
     :.imported-rules-list    {:display :flex :flex-flow "column wrap"
                               :padding (em 2) :background :black}
     :.imported-rules         {
                                :background  :black
                                :color       (rgb 240 240 240)
                                :width       (percent 100)
                                :height      (percent 100)
                                :display     :flex
                                :flex-flow   "row wrap"
                                :font-size   (em 0.7)
                              }
     :.imported-rule         {:display :flex :flex-flow "row wrap" :margin (px 1)}
     :.selector              {:background (rgb 40 40 40) :display :flex :flex-flow "row wrap" :width (em 16) :margin 0 :padding (em 0.5)}
     :.rule                  {:display :flex :width (em 32) :background (rgb 50 50 50) :margin 0}
     :.colours               {:display :flex :flex-flow "row wrap" :background (rgb 100 100 100) :margin (em 1)}
     :.colour-previews       {:display :flex :flex-flow "row wrap" :padding (em 1)}
     :.colour                {:display :flex :min-width (px 8) :min-height (px 8) :max-width (px 32) :max-height (px 32)}
     :.colour-swatch         {:display :flex :min-width (px 24) :min-height (px 24) :max-width (px 64) :max-height (px 64)
                              :margin (px 8)}
     :.css-urls {:display :none}
     :.table
       {
        :padding               (em 1)
        :display               :grid
        :color                 :white
        :grid-template-columns [[(percent 70) (fr 1)]]
        :grid-auto-rows        (em 1.5)
        :grid-row-gap          (em 0.7)
        :grid-column-gap       (em 2)
        }
     ".things"
                             {
                              :display               :grid
                              :grid-area             :content
                              :grid-template-columns [[(percent 20) (fr 1)]]
                              :grid-column-gap       (em 1)
                              :grid-auto-rows        :auto
                              :grid-row-gap          (em 2.5)
                              :background            (rgb 200 205 210)
                              :padding               (percent 4)
                              }
     :body                   {

                              :background                (rgb 30 30 30)
                              :font-family               ["Gill Sans" "Helvetica" "Verdana" "Sans Serif"]
                              :font-size                 (em 1)
                              :font-weight               :normal
                              :cursor                    :default
                              :zoom                      0.8
                              }
     :.button                {
                              :cursor      :pointer
                              :width       (px 10)
                              :height      (px 28)
                              :margin-left (percent 1)
                              }
     ".padding_em-1"         {:padding (em 1)}
     ".button-refresh:hover" {}
     "#text-demo"            {
                              :color (hsl 20 30 90)

                              }
     :div.canvas-parameters  {
                              :max-height (px 200)
                              :padding    (px 10)
                              }
     :div.button-parameters  {
                              :height  (percent 50)
                              :padding (px 10)
                              }

     }))


(defn add-units-rules [state]
  (assoc-in state [:css :units]
    {:.unit    {
                :justify-self    :start
                :display         :flex
                :justify-content :center
                :align-items     :center
                :min-width       (em 2)
                :border-radius   (px 4)
                :border          :none
                :background      (rgb 150 150 150)
                :padding         (em 0.1)
                }
     :.em      {:background (hsl 150 50 50) :color (hsl 150 20 10)}
     :.px      {:background (hsl 30 30 70) :color (hsl 15 20 10)}
     :.percent {:background (hsl 50 70 80) :color (hsl 15 20 10)}
     :.fr      {:background (hsl 200 70 80) :color (hsl 15 20 10)}}))


(defn add-animation-rules [state]
  (assoc-in state [:css :animation]
    [
    (gt/->CSSAtRule :keyframes
      {:identifier :gradient-flow
       :frames     [
                    [:0% {:background (linear-gradient "to top" (rgb 100 100 100) (rgb 255 255 255))}]
                    [:50% {:background (linear-gradient "to top" (rgb 255 255 255) (rgb 150 150 150))}]
                    [:100% {:background (linear-gradient "to top" (rgb 100 100 100) (rgb 255 255 255))}]]})
    ]))