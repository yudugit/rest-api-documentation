<html><body>

<h1>Yudu REST API test request builder</h1>
This page can be used for manually composing requests for the Yudu REST API.
See <a href="examples.php">examples.php for examples</a> of how to programmatically access the API.

<?php

$yuduConfig['key'] = "Please contact Yudu to obtain this key.";
$yuduConfig['sharedSecret'] = "Please contact Yudu to obtain this sharedSecret.";
$yuduConfig['serviceUrl'] = "https://api.yudu.com/Yudu/services/1.0";
$yuduConfig['nodeId'] = "33136";

// The debugging option controls whether information about the REST API request and responses are
// displayed in the standard PHP output.
$yuduConfig['debug'] = true;

$method = "GET";
$queryString  = "";
$path = "/editions/";
$postData = "";

if (array_key_exists('key', $_POST))
{
    $yuduConfig['key'] = $_POST['key'];
}
if (array_key_exists('sharedSecret', $_POST))
{
    $yuduConfig['sharedSecret'] = $_POST['sharedSecret'];
}
if (array_key_exists('serviceUrl', $_POST))
{
    $yuduConfig['serviceUrl'] = $_POST['serviceUrl'];
}
if (array_key_exists('method', $_POST))
{
    $method = $_POST['method'];
}
if (array_key_exists('queryString', $_POST))
{
    $queryString = $_POST['queryString'];
}
if (array_key_exists('postData', $_POST))
{
    $postData = $_POST['postData'];
}
if (array_key_exists('path', $_POST))
{
    $path = $_POST['path'];
}
?>

<form method="post">
    Key: <input name="key" value="<?php echo $yuduConfig['key'] ?>"><br>
    Secret: <input name="sharedSecret" value="<?php echo $yuduConfig['sharedSecret'] ?>"><br>
    Service url: <input name="serviceUrl" value="<?php echo $yuduConfig['serviceUrl'] ?>"><br>
    Path: <input name="path" value="<?php echo $path ?>"><br>
    Request method: <select name="method">
        <option>GET</option>
        <option>POST</option>
        <option>PUT</option>
        <option>DELETE</option>
        <option>OPTIONS</option>
    </select><br>
    Query string:  <input name="queryString" value="<?php echo $queryString ?>"><br>
    Post data:  <textarea rows="10" cols="40" name="postData"><?php echo $postData ?></textarea><br>
    <input type="submit">
</form>

<?php
if (array_key_exists('serviceUrl', $_POST))
{
    require_once 'yuduapi/yuduapi.php';

    parse_str($queryString, $queryVariables);
    sendYuduRequest($_POST['method'], $yuduConfig['serviceUrl'] . $path, $queryVariables, $postData, $yuduConfig);
}
?>
</body></html>
