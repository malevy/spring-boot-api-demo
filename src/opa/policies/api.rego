package api

default allow = false

allow {
    input.user.isAuthenticated == true
}
