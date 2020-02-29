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
           :user-select :text
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
   ".main"
   {
    :background (rgb 10 10 10)
    :width (% 100)
    :height (% 100)
    :display :grid
    :grid-template-columns [[(fr 1) (% 80) (fr 1)]]
    :grid-template-rows (repeet (ï 3) (fr 1))
    :grid-template-areas (areas '[[a title c]
                                  [d params f]
                                  [g footer i]])
    :justify-items :center
    :align-items :center
    }
   ".title"
   {
    :grid-area :title
    :color (rgb 255 255 255)
    }
   ".params"
   {
    :grid-area :params
    :width (% 70)
    :height (% 100)
    :display :flex
    :flex-flow [[:column :wrap]]
    :justify-content :space-between
    :align-items :space-between
    :padding (px 16)
    :border-radius (px 3)
    :background (rgba 100 100 100 0.5)
    :color (rgb 255 255 255)
    }
   ".param"
   {
    :display :flex
    :flex-flow [[:column :wrap]]
    :justify-content :space-between
    :align-items :space-between
    :padding (px 8)
    :border-radius (px 3)
    :background (rgba 100 100 100 0.5)
    :color (rgb 255 255 255)
    }
   ".param-name-value"
   {
    :display :flex
    :flex-flow [[:row :nowrap]]
    :justify-content :space-between
    }
   ".range-input"
   {
    :width (% 100)
    }
   ".text-input"
   {
    :background (rgb 40 40 40)
    :color (rgb 255 255 255)
    :padding (em 1)
    :border :none
    :border-radius (px 2)
    :resize :vertical
    }
   ".animal"
   {
    :width (px 64)
    :height (px 64)
    :top 0 :left 0
    :font-size (px 64)
    :background :none
    :position :absolute
    }
   ".ani-rotating-gradient-0"
   {
    :animation-name :rotating-gradient-0
    :animation-duration (ms 2000)
    :animation-iteration-count :infinite
    :animation-timing-function :linear
    }
   ".ani-rotating-gradient-1"
   {
    :animation-name :rotating-gradient-1
    :animation-duration (ms 2000)
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
        (range 0 1 0.01))})
    (gt/->CSSAtRule :keyframes
     {:identifier :rotating-gradient-1
      :frames
      (mapv
        (fn [t]
          [(str (* t 100) "%")
           {:background
            (apply (partial repeating-linear-gradient (str (* t 360) "deg"))
              [[(rgb 50 50 50) "0px"] [(rgb 50 50 50) (str (int (+ 8 (* t -8))) "px")] [(rgb 255 255 255) "8px"] [(rgb 255 255 255) "16px"]])}])
        (range 1 0 -0.01))})
   ])

(defn css-rules [params]
  (-> {}
    (assoc :main      (main-rules params))
    (assoc :component (component-rules params))
    (assoc :animation (animation-rules params))))