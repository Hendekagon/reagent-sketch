(ns rs.messaging
  "
   Handling messages to/from server,
   and internally

   WebSocket/async pub/sub

  "
  (:require-macros [cljs.core.async.macros :as asm :refer [go alt! go-loop]])
  (:require
    [cljs.reader :as reader]
    [cljs.core.async :as async :refer [<! >! put! chan close! pub sub partition partition-by sliding-buffer tap mult]]
    [goog.events :as events]
    [applied-science.js-interop :as j]
    [garden.color]
    [garden.types])
  (:import [goog.net WebSocket]))

(def more-readers
  {
   :readers
    {
      'garden.color.CSSColor garden.color/map->CSSColor
      'garden.types.CSSUnit  garden.types/map->CSSUnit
      'garden.types.CSSAtRule garden.types/map->CSSAtRule
      'garden.types.CSSFunction garden.types/map->CSSFunction
    }})

(defn subscribe!
  "Subscribe the given component to its topic with its handler"
  ([{{:keys [message-channel message-pub]} :messaging :as state} topic f]
   (let [c (chan)]
     (sub message-pub topic c)
     (go (loop [] (f (<! c)) (recur)))
     state))
  ([state component]
   (doseq [{:keys [topic handler]} ((:subscriptions component) state)]
     (subscribe! state topic handler))))

(defn add-events!
  "Adds all the event listeners to the given websocket"
  ([on-opened on-closed {{:keys [ws ws-uri read-channel write-channel]} :messaging :as s}]
    (events/listen ws
      #js[WebSocket.EventType.CLOSED
          WebSocket.EventType.ERROR
          WebSocket.EventType.MESSAGE
          WebSocket.EventType.OPENED]
      (fn [e]
        (let [t (j/get e :type)]
          (cond
            (= t WebSocket.EventType.MESSAGE)
             (put! read-channel (j/get e :message))
            (= t WebSocket.EventType.OPENED)
             (do
               (println "---websocket opened---")
               (go (loop [] (let [m (<! write-channel)] (j/call ws :send (pr-str m))) (recur)))
               (when on-opened (on-opened s)))
            (= t WebSocket.EventType.CLOSED)
             (do
               (println "ws closed")
               (when on-closed (on-closed s)))
            :otherwise
             (ex-info "websocket error:" e)))))
      (j/assoc! js/window :onbeforeunload
        (fn [] (println "page reloaded, closing websocket") (close! read-channel) (j/call ws :close)))
      s))

(defn add-messaging!
  "
    Configures messaging channels for the application:

    * ws a websocket for talking with the server
    * msgs for inter-component messaging
    *
    * TODO: https only please
  "
  ([state]
    (add-messaging! state nil nil))
  ([state on-opened on-closed]
    (let [
          ws  (WebSocket.)
          rch (chan)
          wch (chan)
          doc-uri (j/get js/window :location)
          ws-uri (str (if (= (j/get doc-uri :protocol) "http:") "ws://" "wss://") (j/get doc-uri :host) "/ws")
          mch (chan 16)
          msgs (pub mch :topic)
          messaging {:message-channel mch :message-pub msgs :ws ws :chatting? false
                     :read-channel rch :write-channel wch :ws-uri ws-uri}
          s (assoc state :messaging messaging)
          ]
      (add-events! on-opened on-closed s)
      (go-loop []
        (try
          (let [m (reader/read-string more-readers (<! rch))]
           (put! mch m))
         (catch js/Error e (println "* error" e)))
        (recur))
      (j/call ws :open ws-uri)
    s)))

