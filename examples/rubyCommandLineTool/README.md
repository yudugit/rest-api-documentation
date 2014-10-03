Yudu Publisher REST API Ruby Command Line Signature Calculator
==============================================================

This is a very basic command line tool to calculate the Base64 encoded HMAC SHA256 hash of a string with a given secret.

To use this command line too you will need to make sure you have ruby installed on your system (at least version 1.8.7).

To run the tool from your command prompt (either in Windows or any Unix-like OS), simply CD to this directory and run `ruby sign.rb` with two parameters: the first is the string to sign and the second is the shared secret.

e.g. `ruby sign.rb foo bar`

This should output a response like

```
Signing string 'foo' with secret 'bar'
FHkzIYqqvAuLEKKzpcNGhMjZQ0G88QpHNtxycPd0GFE=
```
