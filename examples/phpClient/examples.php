<html>
<body>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Introduction</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

This example file demonstrates the use of the Yudu REST API from PHP, by running through an example test case
of creating a reader and giving them permission to view a given edition, then subscribing them to a subscription
containing that edition. The examples should be viewed in conjunction with the Yudu REST API guide which provides
a detailed description of the service interface.

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Configuration</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

First we set up some constants that are used for accessing the API. These configuration options are used by
yuduapi.php, and can all be obtained directly from Yudu.

<?php

$yuduConfig['key'] = "Please contact Yudu to obtain this key.";
$yuduConfig['sharedSecret'] = "Please contact Yudu to obtain this sharedSecret.";
$yuduConfig['serviceUrl'] = "https://api.yudu.com/Yudu/services/1.0";
$yuduConfig['nodeId'] = "33137";

// The debugging option controls whether information about the REST API request and responses are
// displayed in the standard PHP output.
$yuduConfig['debug'] = true;

?>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Dependencies</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

These examples use the PHP HTTP_Request2 package for sending the different types of HTTP requests.
We suggest installing the PEAR package for this library (http://pear.php.net/package/HTTP_Request2).

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Yudu REST API example client</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

All requests to the Yudu REST API need to be authenticated in the same manner, and each returns an XML
document that needs to be parsed by PHP. Therefore we import a common PHP function, sendYuduRequest, to handle
authentication and encoding and sending the request, defined in yuduapi/yuduapi.php.

<?php require_once 'yuduapi/yuduapi.php'; ?>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: To list available edition names published after 2010</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

$postData = "";
$queryVariables = array();
$queryVariables['publishedDate_after'] = '2010-01-01';

$editions = sendYuduRequest('GET', $yuduConfig['serviceUrl'] . '/editions/', $queryVariables, $postData, $yuduConfig);

foreach ($editions->edition as $edition)
{
    echo "<p>" . $edition->name . " was published on " . $edition->publishedDate . "</p>";
}

// We record the id of a specific edition, assuming one exists, for use later
$editionId = $editions->edition[0]->attributes()->id;
?>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: Creating a reader</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

// Create a unique string for identifying a fresh new user.
$uniqid = uniqid();

// Create XML representation of new reader
$yuduNamespace = "http://schema.yudu.com";
$dom = new DomDocument();

$reader = $dom->createElementNS($yuduNamespace, "reader");
$dom->appendChild($reader);

$username = $dom->createElement("username");
$username->appendChild($dom->createTextNode("example$uniqid"));
$reader->appendChild($username);

$emailAddress = $dom->createElement("emailAddress");
$emailAddress->appendChild($dom->createTextNode("user$uniqid@example.com"));
$reader->appendChild($emailAddress);

$firstName = $dom->createElement("firstName");
$firstName->appendChild($dom->createTextNode("firstname$uniqid"));
$reader->appendChild($firstName);

$lastName = $dom->createElement("lastName");
$lastName->appendChild($dom->createTextNode("lastName$uniqid"));
$reader->appendChild($lastName);

$password = $dom->createElement("password");
$password->appendChild($dom->createTextNode("password$uniqid"));
$reader->appendChild($password);

$nodeId = $dom->createElement("nodeId");
$nodeId->appendChild($dom->createTextNode($yuduConfig['nodeId']));
$reader->appendChild($nodeId);

$queryVariables = array();
$postData = $dom->saveXML();

// Send request and record URI for new reader location
$readerUri = sendYuduRequest('POST', $yuduConfig['serviceUrl'] . '/readers/', $queryVariables, $postData, $yuduConfig);
?>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: Get reader details</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

// We use the reader URI that we received from the previous example
$postData = "";
$queryVariables = array();
$reader = sendYuduRequest('GET', $readerUri, $queryVariables, $postData, $yuduConfig);
$readerId = $reader->attributes()->id;

?>
<br>
Username: <?php echo $reader->username; ?><br>
Email Address: <?php echo $reader->emailAddress; ?><br>
First Name: <?php echo $reader->firstName; ?><br>
Last Name: <?php echo $reader->lastName; ?><br>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: Get edition details</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

// We get the details of the edition using the edition id that we obtained in the first example
$postData = "";
$queryVariables = array();
$edition = sendYuduRequest('GET', $yuduConfig['serviceUrl'] . '/editions/' . $editionId, $queryVariables, $postData, $yuduConfig);

?>
<br>
Name: <?php echo $edition->name; ?><br>
Published date: <?php echo $edition->publishedDate; ?><br>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: Add permission for reader to view edition</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

// We use the reader and edition that we obtained in the previous examples

// Create XML representation of the new permission
$dom = new DomDocument();

$permission= $dom->createElementNS($yuduNamespace, "permission");
$dom->appendChild($permission);

$reader = $dom->createElement("reader");
$reader->setAttribute("id", $readerId);
$permission->appendChild($reader);

$edition = $dom->createElement("edition");
$edition->setAttribute("id", $editionId);
$permission->appendChild($edition);

$queryVariables = array();
$postData = $dom->saveXML();

// Send request and record URI for new reader location
$permissionUri = sendYuduRequest('POST', $yuduConfig['serviceUrl'] . '/permissions/', $queryVariables, $postData, $yuduConfig);
?>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: Deny permission for reader to view edition</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

// We delete the permission that we created in the previous example
$postData = "";
$queryVariables = array();
sendYuduRequest('DELETE', $permissionUri, $queryVariables, $postData, $yuduConfig);

?>


<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: List available subscriptions containing the previously found edition</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

$postData = "";
$queryVariables = array();
$queryVariables['edition'] = (string)$editionId;
$subscriptions = sendYuduRequest('GET', $yuduConfig['serviceUrl'] . '/subscriptions/', $queryVariables, $postData, $yuduConfig);

foreach ($subscriptions->subscription as $subscription)
{
    echo "<p>" . $subscription->title . " is a " . $subscription->subscriptionType . " subscription </p>";
}

// We record the id of a specific subscription, assuming one exists, for use later
$subscriptionId = $subscriptions->subscription[0]->attributes()->id;
?>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: Get subscription details</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

// We get the details of the subscription using the subscription id that we obtained above
$postData = "";
$queryVariables = array();
$subscription = sendYuduRequest('GET', $yuduConfig['serviceUrl'] . '/subscriptions/' . $subscriptionId, $queryVariables, $postData, $yuduConfig);

?>
<br>
Title: <?php echo $subscription->title; ?><br>
Type: <?php echo $subscription->subscriptionType; ?><br>
Device Limit: <?php echo $subscription->defaultAuthorisedDeviceLimit; ?><br>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: Add an open ended subscription period for reader to the subscription</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

// We use the reader and subscription that we obtained in the previous examples

// Create XML representation of the new subscription period
$dom = new DomDocument();

$subscriptionPeriod= $dom->createElementNS($yuduNamespace, "subscriptionPeriod");
$dom->appendChild($subscriptionPeriod);

$reader = $dom->createElement("reader");
$reader->setAttribute("id", $readerId);
$subscriptionPeriod->appendChild($reader);

$subscription = $dom->createElement("subscription");
$subscription->setAttribute("id", $subscriptionId);
$subscriptionPeriod->appendChild($subscription);

$startDate = $dom->createElement("startDate");
$startDate->appendChild($dom->createTextNode("2014-01-01"));
$subscriptionPeriod->appendChild($startDate);

$queryVariables = array();
$postData = $dom->saveXML();

// Send request and record URI for new subscription period location
$subscriptionPeriodUri = sendYuduRequest('POST', $yuduConfig['serviceUrl'] . '/subscriptionPeriods/', $queryVariables, $postData, $yuduConfig);
?>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: Get subscription period details</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

// We get the details of the subscription period using the subscription period URI that we obtained above
$postData = "";
$queryVariables = array();
$subscriptionPeriod = sendYuduRequest('GET', $subscriptionPeriodUri, $queryVariables, $postData, $yuduConfig);

$subscriptionPeriodId = $subscriptionPeriod->attributes()->id;

?>
<br>
Reader: <?php echo $subscriptionPeriod->reader->attributes()->id; ?><br>
Subscription: <?php echo $subscriptionPeriod->subscription->attributes()->id; ?><br>
Start date: <?php echo $subscriptionPeriod->startDate; ?><br>
Expiry date: <?php echo $subscriptionPeriod->expiryDate; ?><br>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: Add an end date to the subscription period</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

// Create XML representation of the new subscription period
$dom = new DomDocument();

$subscriptionPeriod = $dom->createElementNS($yuduNamespace, "subscriptionPeriod");
$subscriptionPeriod->setAttribute("id", $subscriptionPeriodId);
$dom->appendChild($subscriptionPeriod);

$expiryDate = $dom->createElement("expiryDate");
$expiryDate->appendChild($dom->createTextNode("2014-12-31"));
$subscriptionPeriod->appendChild($expiryDate);

$queryVariables = array();
$postData = $dom->saveXML();

// Send the request and display the result
$subscriptionPeriod = sendYuduRequest('PUT', $subscriptionPeriodUri, $queryVariables, $postData, $yuduConfig);

?>
<br>
Reader: <?php echo $subscriptionPeriod->reader->attributes()->id; ?><br>
Subscription: <?php echo $subscriptionPeriod->subscription->attributes()->id; ?><br>
Start date: <?php echo $subscriptionPeriod->startDate; ?><br>
Expiry date: <?php echo $subscriptionPeriod->expiryDate; ?><br>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: Delete subscription period</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

$postData = "";
$queryVariables = array();
$return = sendYuduRequest('DELETE', $subscriptionPeriodUri, $queryVariables, $postData, $yuduConfig);

?>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: Delete reader</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

$postData = "";
$queryVariables = array();
$return = sendYuduRequest('DELETE', $readerUri, $queryVariables, $postData, $yuduConfig);

?>

<!-- ------------------------------------------------------------------------------------------------------------ -->
<h1>Example: Exception handling - deleting non-existent reader</h1>
<!-- ------------------------------------------------------------------------------------------------------------ -->

<?php

// The reader at $readerUri has already been deleted by the previous example
// sendYuduRequests throws a YuduApiException
try
{
  $postData = "";
  $queryVariables = array();
  $reader = sendYuduRequest('DELETE', $readerUri, $queryVariables, $postData, $yuduConfig);
}
catch (YuduApiException $e)
{
    print "Error type: " . $e->getErrorType() . ", Error message: " . $e->getMessage();
}

?>

</body></html>
