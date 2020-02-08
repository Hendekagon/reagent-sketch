(ns rs.style
  (:require
    [garden.color :as color :refer [hsl hsla rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pc pt em ms]]
    [garden.types :as gt]
    [garden.arithmetic :as ga]
    [rs.conversion :refer [str-number]]
    [rs.css :refer [fr vw vh % item repeating-linear-gradient calc repeet areas]]))

(defn main-rules [params]
  {
   ".root"
   {
    :background (rgb 30 30 30)
    :display :flex
    :width (vw 100)
    }
   ".main"
   {
    :background (rgb 30 30 30)
    :color (hsl 0 0 100)
    :width (% 100)
    :border-color (rgb 255 0 0)
    :border-width (px 0)
    :border-style :solid
    :display :flex
    :flex-flow '[[row nowrap]]
    :font-family ["Gill Sans" "Helvetica" "Sans Serif"]
    :font-weight :normal
    :font-size (em 1)
    }
   "body" {
           :background (rgb 30 30 30)
           :font-family ["Gill Sans" "Helvetica" "Verdana" "Sans Serif"]
           :font-size (em 1)
           :font-weight :normal
           :cursor :default
           :padding 0
           :margin 0
           :overflow-x :hidden
           }
   })

(defn component-rules [params]
  {
   ".params"
   {
    :padding (em 1)
    :width (% 50)
    :display :flex
    :flex-flow '[[column nowrap]]
    }
   ".range-input"
   {
    :width (% 100)
    }
   ".text-input"
   {
    :padding (em 0.5)
    :background (rgb 10 10 10)
    :color (rgb 255 255 255)
    :border :none
    :width (% 100)
    :height (em 16)
    :resize :vertical
    :font-size (em 1)
    }
   ".param"
   {
    :display :flex
    :flex-flow '[[row nowrap]]
    :justify-content :center
    :align-items :center
    :width (% 100)
    :margin-bottom (em 1)
    }
   ".param-name"
   {
    :font-size (em 1)
    :height (em 2)
    :width (% 30)
    :display :flex
    :align-items :center
    }
   ".param-value"
   {
    :font-size (em 1)
    :width (% 70)
    :display :flex
    :align-items :center
    }
   ".preview"
   {
    :background (rgb 50 50 50)
    :padding (em 1)
    :width (% 50)
    }
   ".columns"
   {
    :background (rgb 50 50 50)
    :column-count (-> (item 3) (assoc :min 1 :max 5 :step 1 :name "Columns"))
    :column-gap (assoc (% 2) :min 1 :max 16 :step 0.1 :name "Column gap")
    }
   ".preview>div"
   {
    :margin-top (em 1)
    }
   ".number"
   {
    :font-size (em 4)
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