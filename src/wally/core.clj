(ns wally.core
  (:import [java.io PrintWriter]))

(def white-list-namespaces
  #{"clojure.core"
    "clojure.set"
    "clojure.uuid"
    "clojure.walk"
    "clojure.repl"
    "clojure.string"})

(def print-doc (var-get #'clojure.repl/print-doc))

(defn- bang-fns-pred [fvar]
  (not (.endsWith (str fvar) "!")))

(defn- is-match? [f output inputs]
  (try
    (if (fn? f)
      (= (apply f inputs) output)
      false)
    (catch Throwable _ false)))

(defn- safe-var-get [var]
  (try
    (var-get var)
    (catch Exception _ nil)))

(defn- filter-namespaces []
  (->> (all-ns)
       (filter #(some #{(str %)} white-list-namespaces))))

(defn- filter-publics [nss]
  {:pre [(seq nss)]}
  (->> nss
       (map ns-publics)
       (map vals)
       (flatten)
       (filter bang-fns-pred)))

(defn find-by-sample-for-namespaces-raw
  "Like `find-by-sample-for-namespaces` but returns the vars without printing anything."
  [nss output & inputs]
  (let [fns (filter-publics nss)]
    (binding [*out* (PrintWriter. "/dev/null")
              *err* (PrintWriter. "/dev/null")]
      ;; use filterv as we don't want the seq to be lazy
      (filterv #(is-match? (safe-var-get %) output inputs) fns))))

(defn find-by-sample-for-namespaces
  "Go through all supplied namespaces and execute all functions
looking for matching outputs for the inputs. This functions is supposed to
find pure (referential transparent) functions.
Choose the namespaces wisely as every function will be executed.
Does not execute functions ended in ! as they normally have side effects.
Ex: (find-by-sample-for-namespaces [\"clojure.core\"] {1 1, 2 3, 3 1, 4 2} [1 2 3 4 4 2 2])
=> -------------------------
   clojure.core/frequencies
   ([coll])
     Returns a map from distinct items in coll to the number of times
     they appear."
  [nss output & inputs]
  (let [samples (apply find-by-sample-for-namespaces-raw nss output inputs)]
    (if (seq samples)
      (doseq [s samples]
        (print-doc (meta s)))
      (println "No functions found for combination of inputs and output"))))

(defn find-by-sample
  "Like find-by-sample-for-namespaces but look only in the white listed namespaces:
`white-list-namespaces` var"
  [output & inputs]
  (apply find-by-sample-for-namespaces (filter-namespaces) output inputs))