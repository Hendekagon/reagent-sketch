# UI Tutorial

A minimalistic sketch of Reagent code

This shows a basic single page application which
has a few HTML components and some dynamic CSS components

This project demonstrates how to make a basic Reagent UI using the very latest techniques (late 2018) and with the fewest dependencies

### What it does

It displays a table of things that change the application's state:

* a text box that changes some text
* a range slider that changes a number
* another range slider that chooses a colour
* some circles
* a fancy table with dynamic CSS layout

CSS is handled completely by Garden, both statically and dynamically in a few different ways.

The application is represented by 1 Reagent atom which contains everything and event handlers (in uitui.views) that delegate to pure functions (in uitui.actions) to change the state as a function of their events

Namespaces:

* `uitui.views` functions for making Reagent views of data. Views are React objects that display data or determine the style of other views
  
* `uitui.actions` functions for changing application state

## Usage

Compiling the Clojurescript with figwheel in a terminal:

`clj -m figwheel.main -b dev -r`

from the root of the project then visit `http://localhost:9501/` (actually Figwheel will probably open it automatically)

`dev.cljs.edn` contains the Clojurescript compilation config

`figwheel-main.edn` contains Figwheel's options

and `deps.edn` contains the project's dependencies

This project can be imported into IntelliJ with:

`File > New > Project from existing sources` and select "deps" for the external model type


## License


Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
