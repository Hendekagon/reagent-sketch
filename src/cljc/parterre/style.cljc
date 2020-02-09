(ns parterre.style
  (:require
    [garden.color :as color :refer [hsl hsla rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pc pt em ms]]
    [garden.types :as gt]
    [garden.arithmetic :as ga]
    [parterre.conversion :refer [str-number]]
    [parterre.css :refer [fr vw vh % ï repeating-linear-gradient calc repeet areas]]))

(defn main-rules [params]
  {
   "body" {
           :background (rgb 0 0 0)
           :font-family ["Gill Sans" "Helvetica" "Verdana" "Sans Serif"]
           :font-size (em 1)
           :font-weight :normal
           :cursor :default
           :padding 0
           :margin 0
           :overflow-x :hidden
           }
   ".root"
   {
    :background (rgb 30 20 10)
    :width (vw 100)
    :height (vh 100)
    :display :flex
    :flex-flow [:row :nowrap]
    :justify-content :center
    :align-items :center
    }
   })

(defn component-rules [params]
  {
   ".range-input"
   {
    :width (px 128)
    }
   ".soil"
   {
    :background (rgb 30 10 10)
    :width  (vh 90)
    :height (vh 90)
    :display :grid
    :grid-template-columns (repeet (ï 3) (% 33.3))
    :grid-template-rows (repeet (ï 3) (% 33.3))
    :grid-template-areas (areas '[[a b c]
                                  [d e f]
                                  [g h i]])
    :justify-items :center
    :align-items :center
    }
   ".hedge"
   {
    :background (rgb 20 40 20)
    :width  (% 80)
    :height (% 80)
    }
   ".parterre-1"
   {
    :background (rgb 90 50 50)
    :width (% 80)
    :height (% 80)
    :grid-area :e
    :display :grid
    :grid-template-columns (repeet (ï 2) (% 50))
    :grid-template-rows    (repeet (ï 2) (% 50))
    :grid-row-gap (assoc (% 2) :min 1 :max 16 :step 0.1)
    :justify-items :center
    :align-items :center
    }
   ".parterre-0"
   {
    :background (rgb 50 90 50)
    :width (% 80)
    :height (% 80)
    :display :grid
    :grid-template-columns (repeet (ï 3) (% 33.3))
    :grid-template-rows    (repeet (ï 3) (% 33.3))
    :grid-column-gap (assoc (% 2) :min 1 :max 16 :step 0.1)
    :justify-items :center
    :align-items :center
    }
   ".ani"
   {
    :animation-name :gradient-flow
    :animation-duration (ms 20000)
    :animation-iteration-count :infinite
    :animation-timing-function :linear
    }
   })

(defn animation-rules [params]
  [
   (gt/->CSSAtRule :keyframes
     {:identifier :gradient-flow
      :frames
      (mapv
        (fn [t]
          [(str (* t 100) "%")
           {:background
            (apply (partial repeating-linear-gradient (str (* t 360) "deg"))
              [[(rgb 50 50 50) "0px"] [(rgb 50 50 50) (str (int (+ 8 (* t -8))) "px")] [(rgb 255 255 255) "8px"] [(rgb 255 255 255) "16px"]])}])
        (range 0 1 0.001))})
   ])

(defn css-rules [params]
  (-> {}
    (assoc :main      (main-rules params))
    (assoc :component (component-rules params))
    (assoc :animation (animation-rules params))))