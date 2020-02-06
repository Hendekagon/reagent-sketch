(ns rs.style
  (:require
    [garden.color :as color :refer [hsl hsla rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pc pt em ms]]
    [garden.types :as gt]
    [garden.arithmetic :as ga]
    [rs.css :refer [fr vw vh p% repeating-linear-gradient calc repeet areas]]))

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
    :width (p% 100)
    :border-color (rgb 255 0 0)
    :border-width (px 1.1)
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
           }
   })

(defn component-rules [params]
  {
   ".params"
   {
    :padding (em 1)
    :width (p% 50)
    :display :flex
    :flex-flow '[[column nowrap]]
    }
   ".range-input"
   {
    ;:width (em 10)
    }
   ".text-input"
   {
    ;:width (em 16)
    }
   ".preview"
   {
    :background (rgb 50 50 50)
    :padding (em 1)
    :width (p% 50)
    :display :flex
    :flex-flow '[[column nowrap]]
    }
    ".preview>div"
    {
      :margin-top (em 1)
    }
   ".ani"
   {
    :animation-name            :gradient-flow
    :animation-duration        (ms 20000)
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