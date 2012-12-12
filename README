clj-mapmyfitness
=============

MapMyFitness API Clojure Wrapper

## Usage
```clj
(ns im.a.happy.namespace
    (:use notch.clj-mapmyfitness))

;;First add your client id and client secret to mapmyfitness.properties.clj

;; Send the user to the auth URL
(def request_token (oauth/request-token consumer "http://localhost"))
(get-auth-uri request_token "http://localhost")

;;The above redirects to something like:
;;http://localhost

;;Then complete oauth by getting an access token
(def access_token (get-access-token request_token))

;;List the user's workouts
(get-workouts access_token)
```

## License

Copyright (C) 2012 Notch, Inc

Distributed under the Eclipse Public License, the same as Clojure.
