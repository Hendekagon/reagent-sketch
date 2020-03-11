# UI Tutorial

A minimalistic tutorial of one way to write a Clojurescript application

This tutorial demonstrates how to write a basic single page application which
has a few HTML components styled with dynamic CSS using the fewest dependencies.

The two concepts I want to emphasize in this tutorial are:

* separation between UI events and the state-updating actions they cause
* style-as-data 

Even if this project's code isn't directy useful to you, I hope these two concepts are.

### What it does

It displays a table of parameters that can be changed that's all

### How it works

In a Clojurescript application, state is usually stored in one atom, which is typically a map containing all the things your application needs to know.
When the user interacts with DOM elements events are generated and we lookup state-updating functions based on these events and update the application state accordingly.

There are two main kinds of code in this project, views and actions:


* `uitui.views` are functions (multimethods) for making Reagent views from data. This is what we see on the screen and can interact with. These views also dispatch events, which describe what the user did: for example {:change :range-slider} is an event that's dispatched together with some other local data when a range-slider is changed
  
* `uitui.actions` are functions for updating application state in response to user events. For example, if a user changes a range-slider's value, we might want to update the corresponding parameter's value. All actions are functions of `[current-application-state and-a-message-about-the-event-which-caused-the-update]`

Here's the flow:

user-interaction -> HTML element's event -> Clojure message -> lookup action -> update state

One consequence of this pattern is that our action and view code is almost entirely pure functions which we can test as we would test any other Clojure code. For example, we can test user events:

`(-> (make-state) (handle-message {:match {:change :range-slider} :message {:path [:params :y :magnitude] :value 127}}))`

in the REPL or unit test. Or we could reduce over a sequence of such test events to get our application into any state. Similarly we can record sequences of user-generated events and replay them later.

And we can test at the level of actions performed:

`(-> (make-state) (update-this message-0) (update-that message-1 ...)`



#### CSS

CSS is handled by Garden, which means there's no CSS files or any dependence on CSS toolchain. All our CSS is stored as plain Clojure data in our app state as separate style modules from which we generate React `style` components. When our style data is updated, so is our CSS so all our CSS is dynamic.
Garden provides proper Units for CSS measures like `px` and `em`. This means we can represent and parameterize our style without any need to manipulate strings. Style-as-data.

---


## Usage

Compiling the Clojurescript with Figwheel in a terminal:

`clj -A:resources -m figwheel.main -b dev -r`

from the root of the project then visit `http://localhost:9500/` (actually Figwheel will probably open it automatically)

`dev.cljs.edn` contains the Clojurescript compilation config

`figwheel-main.edn` contains Figwheel's options

and `deps.edn` contains the project's dependencies

This project can be imported into IntelliJ with:

`File > New > Project from existing sources` and select "deps" for the external model type


---

#### For people who know Reagent/Re-frame etc

The principle is to avoid directly mutating state inside event handlers and to avoid deferencing inside views. It's simply about maximizing the number of pure testable functions. 


## License


Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
