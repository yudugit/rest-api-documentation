Yudu Publisher REST API PHP Example
===================================

Setup
-----

To run these examples you will need to install PHP and Apache. On Windows we recommend installing [WAMP](http://www.wampserver.com/en/), which adds Apache, My-SQL and PHP in a single installation.

You will need to configure Apache to serve the files from this directory. This can be done through WAMP by [adding an alias](http://www.techrepublic.com/blog/smb-technologist/create-aliases-on-your-wamp-server/).

To get the required library dependencies you will need to install the PHP package manager PEAR as described [here](http://bdhacker.wordpress.com/2012/02/18/installing-pear-in-windows-wamp/). If you are missing `go-pear.phar` you can download it [here](http://pear.php.net/go-pear). Once you have installed pear run `pear install HTTP_Request` to install the libraries used for the REST calls. (Please use the older HTTP_Request library to successfully run the example code)

Also ensure that cURL is enabled for your PHP installation by enabling it in your php.ini file or php extensions (which has the name php_curl).

On Windows [Fiddler](http://www.telerik.com/fiddler) is useful to see the XML and other data you are passing over which makes debugging easier.

If you need any support with this please contact support@yudu.com 


Using this example
--------------------

Once you have this example running you can make calls to the Yudu Publisher REST API. This example includes an example client for the API and two PHP pages which can be run through your browser.

* testRequestBuilder.php allows you to build your own REST API calls to get a feel for our API.
* examples.php runs through a sequence of REST API calls demonstrating an example session. **Note:** This example requires a specific set of API keys to run properly. Please contact Yudu to obtain these keys. 
* yuduapi.php is the core file which is required by both examples.php and testRequestBuilder.php - Leaving debug enabled will return useful response information from the server.
