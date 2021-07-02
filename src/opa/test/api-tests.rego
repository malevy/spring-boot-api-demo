# the package name for the tests must match the package name of the rules
package api

test_make_something_work {
    true
}

test_user_must_be_authenticated {
    allow with input as {"user":{"name":"jack", "isAuthenticated":true}}
}