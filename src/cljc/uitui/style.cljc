(ns uitui.style
  (:require
    [garden.color :as color :refer [hsl hsla rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pc pt em ms]]
    [garden.types :as gt]
    [garden.arithmetic :as ga]
    [uitui.conversion :refer [str-number]]
    [uitui.css :refer [fr vw vh % ï repeating-linear-gradient calc repeet areas]]))

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
   ".main"
   {
    :background (rgb 10 10 10)
    :width (% 100)
    :height (% 100)
    :display :grid
    :grid-template-columns [(fr 1) (% 70) (fr 1)]
    :grid-template-rows (repeet (ï 3) (fr 1))
    :grid-template-areas (areas '[[a b c]
                                  [d e f]
                                  [g h i]])
    :justify-items :center
    :align-items :center
    }
   ".params"
   {
    :grid-area :e
    :display :flex
    :flex-flow [[:row :wrap]]
    :justify-content :space-between
    :align-items :space-between
    :padding (px 8)
    :border-radius (px 3)
    :background (rgba 100 100 100 0.5)
    :color (rgb 255 255 255)
    }
   ".param-box"
   {
    :display :flex
    :flex-flow [[:column :nowrap]]
    :justify-content :flex-start
    }
   ".param-name-value"
   {
    :display :flex
    :flex-flow [[:row :nowrap]]
    :justify-content :space-between
    }
   ".ani-rotating-gradient-0"
   {
    :animation-name :rotating-gradient-0
    :animation-duration (ms 20000)
    :animation-iteration-count :infinite
    :animation-timing-function :linear
    }
    ".ani-rotating-gradient-1"
   {
    :animation-name :rotating-gradient-1
    :animation-duration (ms 20000)
    :animation-iteration-count :infinite
    :animation-timing-function :linear
    }
   })

(defn animation-rules [params]
  [
   (gt/->CSSAtRule :keyframes
     {:identifier :rotating-gradient-0
      :frames
      (mapv
        (fn [t]
          [(str (* t 100) "%")
           {:background
            (apply (partial repeating-linear-gradient (str (* t 360) "deg"))
              [[(rgb 50 50 50) "0px"] [(rgb 50 50 50) (str (int (+ 8 (* t -8))) "px")] [(rgb 255 255 255) "8px"] [(rgb 255 255 255) "16px"]])}])
        (range 0 1 0.001))})
    (gt/->CSSAtRule :keyframes
     {:identifier :rotating-gradient-1
      :frames
      (mapv
        (fn [t]
          [(str (* t 100) "%")
           {:background
            (apply (partial repeating-linear-gradient (str (* t 360) "deg"))
              [[(rgb 50 50 50) "0px"] [(rgb 50 50 50) (str (int (+ 8 (* t -8))) "px")] [(rgb 255 255 255) "8px"] [(rgb 255 255 255) "16px"]])}])
        (range 1 0 -0.001))})
   ])

(defn css-rules [params]
  (-> {}
    (assoc :main      (main-rules params))
    (assoc :component (component-rules params))
    (assoc :animation (animation-rules params))))