(ns rs.style
  (:require
    [garden.color :as color :refer [hsl hsla rgb rgba hex->rgb as-hex]]
    [garden.units :as u :refer [defunit px percent pc pt em ms]]
    [garden.types :as gt]
    [garden.arithmetic :as ga]
    [rs.css :refer [fr vw vh p% linear-gradient calc repeet areas]]))

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
   })

(defn animation-rules [params]
  [
   (gt/->CSSAtRule :keyframes
     {:identifier :gradient-flow
      :frames
       [
         [:0% {:background (linear-gradient "to top" (rgb 100 100 100) (rgb 255 255 255))}]
         [:50% {:background (linear-gradient "to top" (rgb 255 255 255) (rgb 150 150 150))}]
         [:100% {:background (linear-gradient "to top" (rgb 100 100 100) (rgb 255 255 255))}]]})
   ])

(defn css-rules [params]
  (-> {}
    (assoc :main      (main-rules params))
    (assoc :component (component-rules params))
    (assoc :animation (animation-rules params))))