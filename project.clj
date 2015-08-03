(defproject om-async "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.0.0"

  :jvm-opts ^:replace ["-Xmx1g" "-server"]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [instaparse "1.4.1"]
                 [org.clojure/clojurescript "0.0-3195"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.omcljs/om "0.8.8"]
                 [garden "1.2.5"]
                 [ring "1.3.2"]
                 [compojure "1.3.1"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [environ "0.5.0"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [org.postgresql/postgresql "9.4-1200-jdbc41"]
                 [org.clojure/tools.logging "0.3.1"]
                 [log4j/log4j "1.2.17"]
                 [com.logentries/logentries-appender "1.1.20"]
                 ;[sablono "0.3.5"]
                 ]

  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-figwheel "0.3.7"]
            ;[lein-garden "0.2.6"]
            [environ/environ.lein "0.2.1"]]

  :hooks [environ.leiningen.hooks
          leiningen.cljsbuild]
  :uberjar-name "missiles.jar"
  :profiles {:production {:env {:production true}}
             :dev        {:dependencies [[ring-mock "0.1.3"]
                                         [midje "1.5.0"]]}}

  :main om-async.core
  :aot [om-async.core]

  :source-paths ["src/clj" "src/cljs"]
  :resource-paths ["resources"]
  :clean-targets ^{:protect false} ["resources/public/js/out"
                                    "resources/public/js/main.js"]

  :figwheel {:ring-handler om-async.core/handler}

  :cljsbuild {:builds [{:source-paths ["src/clj" "src/cljs"]
                        :figwheel     true
                        :compiler     {:main          om-async.core
                                       :output-to     "resources/public/js/main.js"
                                       :output-dir    "resources/public/js/out"
                                       :asset-path    "js/out"
                                       :optimizations :none
                                       :source-map    true}}
                       ;{:id           "release"
                       ; :source-paths ["src"]
                       ; :compiler     {:main          om-sync.core
                       ;                :output-to     "resources/public/js/main.js"
                       ;                :output-dir    "resources/public/js/out"
                       ;                :asset-path    "js/out"
                       ;                :optimizations :advanced
                       ;                :pretty-print  false}}
                       ]}

  ;:garden {:builds [{;; Optional name of the build:
  ;                     :id "screen"
  ; Source paths where the stylesheet source code is
  ;:source-paths ["src/css"]
  ; The var containing your stylesheet:
  ;:stylesheet om-async.screen/screen
  ; Compiler flags passed to `garden.core/css`:
  ;:compiler {;; Where to save the file:
  ;           :output-to "resources/public/css/screen.css"
  ; Compress the output?
  ;:pretty-print? false}}]}
  ;:prep-tasks [["garden" "once"]]
  )
