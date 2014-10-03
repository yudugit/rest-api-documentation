Yudu Publisher REST API v1.0
============================

## Table of contents

- [Introduction](#introduction)
  - [Terminology](#terminology)
  - [Overview](#overview)
  - [Sample code](#sample-code)
  - [Outline](#outline)
  - [Resources](#resources)
- [Link Relations](#link-relations)
  - [http://schema.yudu.com/reader](#httpschemayuducomreader)
  - [http://schema.yudu.com/readers](#httpschemayuducomreaders)
  - [http://schema.yudu.com/edition](#httpschemayuducomedition)
  - [http://schema.yudu.com/editions](#httpschemayuducomeditions)
  - [http://schema.yudu.com/permission](#httpschemayuducompermission)
  - [http://schema.yudu.com/permissions](#httpschemayuducompermissions)
  - [http://schema.yudu.com/readerLogin](#httpschemayuducomreaderlogin)
  - [http://schema.yudu.com/readerLogins](#httpschemayuducomreaderlogins)
  - [http://schema.yudu.com/publication](#httpschemayuducompublication)
  - [http://schema.yudu.com/publications](#httpschemayuducompublications)
  - [http://schema.yudu.com/subscription](#httpschemayuducomsubscription)
  - [http://schema.yudu.com/subscriptions](#httpschemayuducomsubscriptions)
  - [http://schema.yudu.com/subscriptionPeriod](#httpschemayuducomsubscriptionperiod)
  - [http://schema.yudu.com/subscriptionPeriods](#httpschemayuducomsubscriptionperiods)
  - [http://schema.yudu.com/reader/authorisedDevices](#httpschemayuducomreaderauthoriseddevices)
  - [http://schema.yudu.com/reader/authentication](#httpschemayuducomreaderauthentication)
- [XML specifications](#xml-specifications)
  - [Readers](#readers)
  - [Editions](#editions)
  - [Permissions](#permissions)
  - [Reader logins](#reader-logins)
  - [Publications](#publications)
  - [Subscriptions](#subscriptions)
  - [Subscription periods](#subscription-periods)
  - [Authorised Devices](#authorised-devices)
  - [Reader Authentication](#reader-authentication)
  - [Links](#links)
  - [Authentication](#authentication)
  - [Exceptions](#exceptions)
- [Example Sessions](#example-sessions)
  - [Creating a new reader](#creating-a-new-reader)
  - [Finding an edition](#finding-an-edition)
  - [Creating a permission for an existing reader](#creating-a-permission-for-an-existing-reader)
  - [Updating a reader](#updating-a-reader)
  - [Finding an iDevice enabled publication](#finding-an-idevice-enabled-publication)
  - [Finding a subscription](#finding-a-subscription)
  - [Creating a subscription period for an existing reader and subscription](#creating-a-subscription-period-for-an-existing-reader-and-subscription)
  - [Resetting the authorised devices for a reader](#resetting-the-authorised-devices-for-a-reader)
  - [Authenticating a reader](#authenticating-a-reader)

## Introduction

This document outlines the web service interface provided by YUDU to enable management of reader purchases for digital editions.

Access to the service requires a Yudu Publisher account with the "REST API" permission and a valid API token created through the Yudu Publisher interface.

### Terminology

The following terminology is used in this document:

- **Reader** - An end user that will be reading your digital editions
- **Edition** - A Yudu digital edition
- **Permission** - Refers to the granting of access to one edition for one reader
- **Reader login** - A particular instance of a reader accessing an edition
- **Publication** - A Yudu publication
- **Subscription** - A Yudu subscription
- **Subscription period** - Refers to the granting of access to a subscription for a time period
- **Node** - The Yudu Publisher system is arranged into a hierarchy of nodes. For most users you won't need to worry about the node ID of your Readers, however if you would like to place them at different levels within your part of the hierarchy you can by specifying it.

### Overview

The Yudu API uses the "Representational state transfer" (REST) architectural style. In particular, it applies the "hypermedia as the engine of application state" (HATEOAS) principle in the design of the resources. If you are not already familiar with these principles then we recommend reading [REST in Practice](http://restinpractice.com/book/) as an introduction before diving further into the Yudu API.

### Sample code

We have produced a sample PHP application that shows the use of the API. Note that this code is not ready for production use, and serves as an example only. It is not extensively tested and does not handle every possible error case in a suitable manner for production use. In addition, it does not represent best practices for implementing a client of the API. For example, as noted below, rather than using the URIs as described, you are encouraged to make use of the hypermedia present in the resources to navigate the API, decoupling your implementation from ours.

We have also produced a simple ruby command line tool to calculate the correct Base64 encoded HMAC SHA256 hash for any string and shared secret (see [Authentication](#authentication)). This can be used to check that your signing method is creating the correct signature.

All code samples can be found the [examples](examples) directory and each is accompanied by a README.md file which contains the documentation.

## Outline

The web services are based on a REST architecture. Resources, such as readers or their permissions, are given a unique URI, and these are operated on by using different HTTP methods, such as **GET**, **POST**, **PUT** and **DELETE**.

### Resources

The following table summarises all the available resource URIs, and the effect of each HTTP method on them. Each of them is relative to the base URI for our webservice: `https://api.yudu.com/Yudu/services/1.0`.

| Resource                        | GET                                                 | POST                                  | PUT                             | DELETE                                      |
| ------------------------------- | --------------------------------------------------- | ------------------------------------- | ------------------------------- | ------------------------------------------- |
| /                               | Returns a list of links to the other available URIs | N/A                                   | N/A                             | N/A                                         |
| /readers/                       | Returns a list of readers                           | Creates a new reader                  | N/A                             | N/A                                         |
| /readers/<id>                   | Returns the details of a single reader              | N/A                                   | Updates a reader                | Deletes a reader                            |
| /editions/                      | Gets a list of all editions                         | N/A                                   | N/A                             | N/A                                         |
| /editions/<id>                  | Gets the details of a single edition                | N/A                                   | N/A                             | N/A                                         |
| /permissions/                   | Lists all edition permissions by readers            | Creates a new permission for a reader | N/A                             | N/A                                         |
| /permissions/<id>               | Gets the details of a single permission             | N/A                                   | Updates a permission            | Removes an existing permission              |
| /readerLogins/                  | Gets a list of all reader logins                    | N/A                                   | N/A                             | N/A                                         |
| /readerLogins/<id>              | Gets the details of a single reader login           | N/A                                   | N/A                             | N/A                                         |
| /publications/                  | Gets a list of all publications                     | N/A                                   | N/A                             | N/A                                         |
| /publications/<id>              | Gets the details of a single publication            | N/A                                   | N/A                             | N/A                                         |
| /subscriptions/                 | Gets a list of subscriptions                        | N/A                                   | N/A                             | N/A                                         |
| /subscriptions/<id>             | Gets the details of a single subscription           | N/A                                   | N/A                             | N/A                                         |
| /subscriptionPeriods/           | Gets a list of subscription periods                 | N/A                                   | N/A                             | N/A                                         |
| /subscriptionPeriods/<id>       | Gets the details of a single subscription period    | Creates a new subscription period     | Updates a subscription period   | Removes an existing subscription period     |
| /readers/<id>/authorisedDevices | N/A                                                 | N/A                                   | N/A                             | Removes all authorised devices for a reader |
| /readers/<id>/authentication    | N/A                                                 | N/A                                   | Authenticates a user's password | N/A                                         |

It is not recommended that these URIs are constructed programmatically. Instead, clients consuming this service should follow links from one resource to another to find the information required. More details are given below.

### Link Relations

When accessing resources, links are given within the XML document that will direct the client to other related resources. To identify supported actions at these resources the following "relation" tags are used:

#### http://schema.yudu.com/reader

A single reader resource.
A **GET** returns that reader's details, and a **PUT** updates them. A **DELETE** request removes the reader and all associated permissions and subscription periods.

#### http://schema.yudu.com/readers

A **GET** request returns a list of readers, optionally filtered using query string parameters:

- **emailAddress** - filter by email address prefix
- **username** - filter by username prefix
- **firstName** - filter by given name prefix
- **lastName** - filter by family name prefix
- **subscription** - return readers subscribed to a given subscription

A **POST** request creates a new reader. For full details see [Readers](#readers).

#### http://schema.yudu.com/edition

A single edition resource.
A **GET** returns the details of the edition. No other operations are supported.

#### http://schema.yudu.com/editions

A **GET** request returns a list of editions, optionally filtered using the following query string parameters:

- **name** - filters by edition name prefix
- **subscription** - filter to show only editions shipped to the given edition
- **publishedDate\_after** - return editions with an official publication date after the given date
- **publishedDate\_before** - return editions with an official publication date before the given date
- **flashPublished** - filter to show only editions published on the flash platform
- **iOSPublished** - filter to show only editions published on the iOS platform
- **androidPublished** - filter to show only editions published on the android/air platform
- **htmlPublished** - filter to show only editions published on the HTML5 platform
- **webPublished** - filter to show only editions published on the combined web platform.

The **<platform>Published** queries require a boolean parameter. Accepted boolean pairs are `t / f`, `true / false`, `y / n`, `yes / no` and `1 / 0`.

Dates must be given in [ISO 8601](http://en.wikipedia.org/wiki/ISO_8601) format.

No other operations are supported.

#### http://schema.yudu.com/permission

A single permission resource.
A **GET** returns the details of the permission, a **DELETE** removes the record of the permission.

#### http://schema.yudu.com/permissions

A **GET** request returns a list of permissions, optionally filtered using the following query string parameters:

- **reader** - filter to show only permissions for a given reader
- **edition** - filter to show only permissions for a given edition
- **creationDate\_after** - return permissions after the given date
- **creationDate\_before** - return permissions before the given date

Dates must be given in [ISO 8601](http://en.wikipedia.org/wiki/ISO_8601) format.

A **POST** request creates a new permission. For full details see [Permissions](#permissions).

#### http://schema.yudu.com/readerLogin

A single reader login resource.
A **GET** returns the details of the reader login. No other operations are supported.

#### http://schema.yudu.com/readerLogins

A **GET** request returns a list of reader logins, optionally filtered using the following query string parameters:

- **reader** - filter to only show reader logins by a particular reader
- **node** - filter to only show reader logins against a particular node
- **loginDate\_after** - return reader logins after the given date
- **loginDate\_before** - return reader logins before the given date
- **platform** - filter to only reader logins against a particular digital platform i.e. *flash*, *idevice* and *air*

Dates must be given in [ISO 8601](http://en.wikipedia.org/wiki/ISO_8601) format.

No other operations are supported.

#### http://schema.yudu.com/publication

A single publication resource.
A **GET** returns the details of the publication. No other operations are supported.

#### http://schema.yudu.com/publications

A **GET** request returns a list of publications, optionally filtered using the following query string parameters:

- **name** - filters by publication name prefix
- **iDeviceEnabled** - filter to return publications which are enabled or disabled for the *idevice* platform.
- **androidEnabled** - filter to return publications which are enabled or disabled for the *android* platform.

The **iDeviceEnabled** and **androidEnabled** queries require a boolean parameter. Accepted boolean pairs are `t / f`, `true / false`, `y / n`, `yes / no` and `1 / 0`.

No other operations are supported.

#### http://schema.yudu.com/subscription

A single subscription resource.
A **GET** returns the details of the subscription. No other operations are supported.

#### http://schema.yudu.com/subscriptions

A **GET** request returns a list of subscriptions, optionally filtered using the following query string parameters:

- **title** - filter by subscription tile
- **onDeviceTitle** - filter by subscription on device title
- **disabled** - filter by whether this subscription is enabled of disabled
- **subscriptionType** - filter by the type of subscription
- **node** - filter to show subscriptions at a particular node
- **reader** - filter to show subscriptions subscribed to a particular reader
- **edition** - filter to show subscriptions which contain a particular edition

The **disabled** query requires a boolean parameter. Accepted boolean pairs are `t / f`, `true / false`, `y / n`, `yes / no` and `1 / 0`.

The **subscriptionType** query requires one of the following values:

- **ios** - iOS subscription
- **ios\_club** - iOS club subscription
- **ios\_node** - iOS node subscription
- **android** - Android subscription
- **android\_club** - Android club subscription
- **android\_node** - Android node subscription 
- **flash** - Flash subscription
- **flash\_club** - Flash club subscription
- **flash\_node** - Flash node subscription
- **universal** - Universal subscription
- **universal\_club** - Universal club subscription

No other operations are supported.

#### http://schema.yudu.com/subscriptionPeriod

A single subscription period resource.
A **GET** returns the details of the subscription period, a **PUT** updates them, and a **DELETE** removes the record of the subscription period.

#### http://schema.yudu.com/subscriptionPeriods

A **GET** request returns a list of subscription periods, optionally filtered using the following query string parameters:

- **reader** - filter to show subscription periods for a particular reader
- **subscription** - filter to show subscription periods for a particular subscription
- **startDate\_after** - return subscription periods starting after the given date
- **startDate\_before** - return subscription periods starting before the given date
- **expiryDate\_after** - return subscription periods expiring after the given date
- **expiryDate\_before** - return subscription periods before after the given date

A **POST** request creates a new subscription period. For full details see [Subscription periods](#subscription-periods).

#### http://schema.yudu.com/reader/authorisedDevices

This resource allows the deletion of all authorised devices associated to a reader using a **DELETE** request. No other methods are supported.

#### http://schema.yudu.com/reader/authentication

This resource allows the authentication of a reader using a **PUT** request. No other methods are supported.

### XML specifications

Each of the resources have an XML schema defined that specify the elements that will be present in responses and those that must be present in a request. Our response content type is "application/vnd.yudu+xml" to specify that this is a vendor specific XML specification. You can find the schemas by issuing an **OPTIONS** request to a resource. The XML returned is a WADL2 describing the service.

The following examples form an informal specification of what you can expect.

Note dates will be in ISO 8601 format, for example "2011-08-05T00:00:00+01:00", but are shortened in examples for brevity.

#### Readers

A reader resource will contain XML like this:

``` xml
<reader id="1234">
  <username>example</username>
  <emailAddress>user@example.com</emailAddress>
  <firstName>Example</firstName>
  <lastName>User</lastName>
  <nodeId>1234</nodeId>
  <authorisedDeviceLimit>3</authorisedDeviceLimit>
  <link rel="http://schema.yudu.com/permissions" name="permissions"
    href="https://api.yudu.com/Yudu/services/1.0/permissions/?reader=1234"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/subscriptions" name="subscriptions"
    href="https://api.yudu.com/Yudu/services/1.0/subscription/?reader=1234"
    type="application/vnd.yudu+xml"
  />
</reader>
```

When creating a reader with a **POST** request you must specify the required elements "username", "emailAddress", "firstName", "lastName" and "password". When creating a reader the "id" attribute must *not* be included as this is generated by the server and returned by the request.

In **GET** requests the password will not be returned for security. A **GET** returning a list of readers will wrap them in a "readers" element.

In **PUT** requests you can include only the fields that you wish to update.

#### Editions

Editions are represented using XML as follows:

``` xml
<edition id="1234">
  <name>example</name>
  <publishedDate>2011-06-15</publishedDate>
  <link rel="http://schema.yudu.com/permissions" name="permissions"
        href="https://api.yudu.com/Yudu/services/1.0/permissions/?edition=1234"
        type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/subscriptions" name="subscriptions"
        href="https://api.yudu.com/Yudu/services/1.0/subscriptions/?edition=1234"
        type="application/vnd.yudu+xml"
  />
</edition>
```

The published date is the "official publication date" of the edition. This is the date used by subscriptions to determine which editions can be accessed by the user.

#### Permissions

Permissions are formatted as follows:

``` xml
<permission id="1234">
  <reader id="123"/>
  <edition id="456"/>
  <creationDate>2011-06-15</creationDate>
  <expiryDate>2012-06-15</expiryDate>
  <link rel="http://schema.yudu.com/reader" name="reader"
        href="https://api.yudu.com/Yudu/services/1.0/readers/123"
        type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/edition" name="edition"
        href="https://api.yudu.com/Yudu/services/1.0/editions/456"
        type="application/vnd.yudu+xml"
  />
</permission>
```

To create a new permission you only need to identify the reader and edition by ID.

The expiry date field is optional and will not appear in responses if not set.

For updates, you can only modify the expiry date field.

#### Reader logins

Reader logins will appear as follows:

``` xml
<readerLogin id="1234">
  <loginDate>2011-08-12</loginDate>
  <reader id="123"/>
  <node id="456"/>
  <platform>flash</platform>
  <emailAddress>user@example.com</emailAddress>
</readerLogin>
```

#### Publications

Publications are represented using XML as follows:

``` xml
<publication id="1234">
  <name>Example</name>
  <iDeviceEnabled>true</iDeviceEnabled>
  <androidEnabled>true</androidEnabled>
</publication>
```

#### Subscriptions

Subscriptions are represented using XML as follows:

``` xml
<subscription id="1234">
  <title>example</title>
  <onDeviceTitle>examples</onDeviceTitle>
  <subscriptionType>flash_node</subscriptionType>
  <disabled>false</disabled>
  <defaultAuthorisedDeviceLimit>6</defaultAuthorisedDeviceLimit>
  <nodeId>456</nodeId>
  <link rel="http://schema.yudu.com/readers" name="readers"
        href="https://api.yudu.com/Yudu/services/1.0/readers/?subscription=1234"
        type="application/vnd.yudu+xml"
  />
</subscription>
```

#### Subscription periods

Subscription periods are represented using XML as follows:

``` xml
<subscriptionPeriod xmlns="http://schema.yudu.com" id="1234">
  <reader id="345" />
  <subscription id="678" />
  <startDate>2011-06-15</startDate>
  <expiryDate>2013-07-03</expiryDate>
  <link rel="http://schema.yudu.com/reader" name="reader"
      href="https://api.yudu.com /Yudu/services/1.0/readers/345"
      type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/subscription" name="subscription"
      href="https://api.yudu.com /Yudu/services/1.0/subscriptions/678"
      type="application/vnd.yudu+xml"
  />
</subscriptionPeriod>
```

#### Authorised Devices

Authorised devices do not have an XML representation as they can only be accesed via a **DELETE** operation.

#### Reader Authentication

An authentication request is represented in XML as follows:

``` xml
<authentication xmlns="http://schema.yudu.com">
  <password>userPassword</password>
</authentication>
```

and an authentication response is represented like this:

``` xml
<authentication xmlns="http://schema.yudu.com">
  <authenticated>true</authenticated>
</authentication>
```

#### Links

A number of `link` elements may nested within any resource. These represent a the next logical resources that should be accessed from the given resource. Their xml representation is as follows:

``` xml
<link rel="http://schema.yudu.com/permissions" name="permissions"
  href="https://api.yudu.com/Yudu/services/1.0/permissions/?reader=1234"
  type="application/vnd.yudu+xml"
/>
```

The attributes in the `link` tag are:
- `rel` - attribute respresents the linked resource type.
- `name` - the name of the link.
- `href` - the URI for the linked resource.
- `type` - the Content Type of the linked resource.

It is reccomended that you use an **Options** call to the URI in the link to determine which operations are allowed on the resource. A list of supported opertations will be contained in the `Allow` header.

### Authentication

Each request must be accompanied by a two piece authentication scheme.

There is an API key and a shared secret that can be obtained and managed through the Yudu Publisher interface. The first is used for identification, and the second for signing.

The API key should be supplied with each request, preferably as a request header `Authentication` but it can also be supplied as a query string parameter of the same name.

The shared secret should be used as the signing token to sign the request using a base-64 encoded HMAC-SHA256 hash of the HTTP method (in upper case), query parameters, sorted into alphabetical order, concatenated with the full body of the request where appropriate (i.e. POST and PUT requests). The query parameters must include `timestamp`, a unix epoch timestamp (in seconds) of the request. For example, given a request URL and query string like this:

`http://api.yudu.com/Yudu/services/1.0/permissions/?timestamp=123456789&reader=1234`

you should generate the HMAC using the string

`GET/Yudu/services/1.0/permissions/?reader=1234&timestamp=123456789`

The result of the signing can then be added as a request header `Signature`, although as for `Authentication` it can also be supplied as a query string parameter called `Signature`.

Query string parameters should be signed before they have been URL encoded.

If you are using query string parameters rather than headers for your authentication then both the API key and the signature should be added after signing.

The timestamp is checked on our server to protect against request replay attacks. You must make sure your server time is set accurately otherwise your requests may be rejected.

### Exceptions

Most errors will be signified by using the relevant HTTP response code, for example a 405 if you attempt to PUT to a resource that does not support it, or a 400 for a bad request. Bad request will contain a response detailing the error, for example, if a bad date parameter is given:

``` xml
<error xmlns="http://schema.yudu.com">
  <code>CLIENT_ERROR</code>
  <detail>Predicate Parsing failed: Could not parse date '123'</detail>
</error>
```

Or for a malformed XML request:

``` xml
<error xmlns="http://schema.yudu.com">
  <code>MALFORMED_REQUEST</code>
  <detail>Unknown element "test"</detail>
</error>
```

If there is a validation failure within the XML, then the response will contain a validation failure code:

``` xml
<error xmlns="http://schema.yudu.com">
  <code>VALIDATION_FAILURE</code>
  <detail> Validation failed: firstName may not be null</detail>
  <validationFailures>
    <failure>
      <cause>NOTNULL</cause>
      <field>firstName</field>
    </failure>
  </validationFailures>
</error>
```

An attempt to create a reader with the same username as an existing one should be met with a 409 ("conflict") response:

``` xml
<error xmlns="http://schema.yudu.com">
  <code>VALIDATION_FAILURE</code>
  <detail>Validation failed: Username is already in use</detail>
  <validationFailures>
    <failure>
      <cause>DUPLICATE_USERNAME</cause>
      <field>username</field>
    </failure>
  </validationFailures>
</error>
```

An attempt to make a duplicate permission would also get a 409 response:

``` xml
<error xmlns="http://schema.yudu.com">
  <code>DUPLICATE_ITEM</code>
  <detail>Item already exists</detail>
</error>
```

Any response in the 500 range indicates a server fault and should be reported to Yudu. A 401 response means you have provided invalid authentication credentials.

## Example Sessions

### Creating a new reader

Request the base URI, which provides links to the other resources:

``` xml
//Request
GET /Yudu/services/1.0/?timestamp=1310378400 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 1234
Signature: 5678

//Response
HTTP/1.1 200 OK
Date: Mon, 11 Jul 2011 10:00:00 GMT
Cache-Control: no-cache
Content-Type: application/vnd.yudu+xml
Content-Length: ...

<service xmlns="http://schema.yudu.com">
  <link rel="http://schema.yudu.com/readers"
    href="https://api.yudu.com/Yudu/services/1.0/readers/"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/editions"
    href="https://api.yudu.com/Yudu/services/1.0/editions/"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/permissions"
    href="https://api.yudu.com/Yudu/services/1.0/permissions/"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/publications"
    href="https://api.yudu.com/Yudu/services/1.0/publications/"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/subscriptions"
    href="https://api.yudu.com/Yudu/services/1.0/subscriptions/"
    type="application/vnd.yudu+xml"
  />
</service>
```

This tells us that there are five links, with the relations defined as above.

We want to create a new reader, and we know from the link relations that we can do this by submitting a **POST** request containing a `reader` to a link of type `http://schema.yudu.com/readers`.

``` XML
//Request
POST /Yudu/services/1.0/readers/?timestamp=1310378405 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 1234
Signature: 9123

<reader xmlns="http://schema.yudu.com">
  <username>example</username>
  <emailAddress>user@example.com</emailAddress>
  <firstName>Example</firstName>
  <lastName>User</lastName>
  <password>password</password>
  <nodeId>1234</nodeId>
</reader>

//Response
HTTP/1.1 201 CREATED
Date: Mon, 11 Jul 2011 10:00:05 GMT
Cache-Control: no-cache
Content-Type: application/vnd.yudu+xml
Content-Length: ...
Location: https://api.yudu.com/Yudu/services/1.0/readers/1234
```

The response has returned the location of our new reader object, so we issue a **GET** to find out what we've got.

``` xml
//Request
GET /Yudu/services/1.0/readers/1234?timestamp=1310378410 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 1234
Signature: 5345

//Response
HTTP/1.1 200 OK
Date: Mon, 11 Jul 2011 10:00:10 GMT
Cache-Control: no-cache
Content-Type: application/vnd.yudu+xml
Content-Length: ...

<reader xmlns="http://schema.yudu.com" id="1234">
  <username>example</username>
  <emailAddress>user@example.com</emailAddress>
  <firstName>Example</firstName>
  <lastName>User</lastName>
  <nodeId>1234</nodeId>
  <link rel="self" name="self"
    href="https://api.yudu.com/Yudu/services/1.0/readers/1234"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/permissions" name="permissions"
    href="https://api.yudu.com/Yudu/services/1.0/permissions/?reader=1234"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/readerLogins" name="readerLogins"
    href="https://api.yudu.com/Yudu/services/1.0/readerLogins/?reader=1234"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/subscriptions" name="subscriptions"
    href="https://api.yudu.com/Yudu/services/1.0/subscription/?reader=1234"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/reader/authorisedDevices" name="authorisedDevices"
    href="https://api.yudu.com/Yudu/services/1.0/readers/1234/authorisedDevices"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/reader/authentication" name="authentication"
    href="https://api.yudu.com/Yudu/services/1.0/readers/1234/authentication"
    type="application/vnd.yudu+xml"
  />
</reader>
```

### Finding an edition

``` xml
//Request
GET /Yudu/services/1.0/editions/?name=Examp&timestamp=1310378415 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 1234
Signature: abcd1234

//Response
HTTP/1.1 200 OK
Date: Mon, 11 Jul 2011 10:00:15 GMT
Cache-Control: no-cache
Content-Type: application/vnd.yudu+xml
Content-Length: ...

<editions>
  <edition xmlns="http://schema.yudu.com" id="5678">
    <name>Example 1</name>
    <publishedDate>2011-01-20</publishedDate>
    <link rel="self" name="self"
      href="https://api.yudu.com/Yudu/services/1.0/editions/5678
      type="application/vnd.yudu+xml"
    />
    <link rel="http://schema.yudu.com/permissions" name="permissions"
      href="https://api.yudu.com/Yudu/services/1.0/permissions/?edition=5678
      type="application/vnd.yudu+xml"
    />
    <link rel="http://schema.yudu.com/subscriptions" name="subscriptions"
      href="https://api.yudu.com/Yudu/services/1.0/subscriptions/?edition=5678"
      type="application/vnd.yudu+xml"
    />
  </edition>
  <edition xmlns="http://schema.yudu.com" id="5679">
    <name>Example 2</name>
    <publishedDate>2011-01-27</publishedDate>
    <link rel="self" name="self"
      href="https://api.yudu.com/Yudu/services/1.0/editions/5679
      type="application/vnd.yudu+xml"
    />
    <link rel="http://schema.yudu.com/permissions" name="permissions"
      href="https://api.yudu.com/Yudu/services/1.0/permissions/?edition=5679
      type="application/vnd.yudu+xml"
    />
    <link rel="http://schema.yudu.com/subscriptions" name="subscriptions"
      href="https://api.yudu.com/Yudu/services/1.0/subscriptions/?edition=5679"
      type="application/vnd.yudu+xml"
    />
  </edition>
</editions>
```

### Creating a permission for an existing reader

Given the above examples, we can now create a permission for this reader:

``` xml
//Request
POST /Yudu/services/1.0/permissions/?timestamp=1310378420 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 1234
Signature: 5345

<permission xmlns="http://schema.yudu.com">
  <reader id="1234"/>
  <edition id="5678"/>
</permission>

//Response
HTTP/1.1 201 CREATED
Date: Mon, 11 Jul 2011 10:00:20 GMT
Cache-Control: no-cache
Content-Type: application/vnd.yudu+xml
Content-Length: ...
Location: https://api.yudu.com/Yudu/services/1.0/permissions/9876
```

And if we get the permission to find out what we've got:

``` xml
//Request
GET /Yudu/services/1.0/permissions/9876?timestamp=1310378430 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 1234
Signature: ab3ba

//Response
HTTP/1.1 200 OK
Date: Mon, 11 Jul 2011 10:00:30 GMT
Cache-Control: no-cache
Content-Type: application/vnd.yudu+xml
Content-Length: ...

<permission xmlns="http://schema.yudu.com" id="9876">
  <reader id="1234"/>
  <edition id="5678"/>
  <link rel="self" name="self"
    href="https://api.yudu.com/Yudu/services/1.0/permissions/9876"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/reader" name="reader"
    href="https://api.yudu.com/Yudu/services/1.0/readers/1235"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/edition" name="edition"
    href="https://api.yudu.com/Yudu/services/1.0/editions/5678"
    type="application/vnd.yudu+xml"
  />
</permission>
```

### Updating a reader

Suppose we need to change a user's password:

``` xml
//Request
PUT /Yudu/services/1.0/readers/1234?timestamp=1310378435 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 1234
Signature: ab3ba

<reader xmlns="http://schema.yudu.com" id="1234">
  <password>newPassword</password>
</reader>

//Response
HTTP/1.1 200 OK
Date: Mon, 11 Jul 2011 10:00:35 GMT
Cache-Control: no-cache
Content-Type: application/vnd.yudu+xml
Content-Length: ...

<reader xmlns="http://schema.yudu.com" id="1234">
  <username>example</username>
  <emailAddress>user@example.com</emailAddress>
  <firstName>Example</firstName>
  <lastName>User</lastName>
  <nodeId>1234</nodeId>
  <link rel="self" name="self"
    href="https://api.yudu.com/Yudu/services/1.0/readers/1234"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/permissions" name="permissions"
    href="https://api.yudu.com/Yudu/services/1.0/permissions/?reader=1234"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/readerLogins" name="readerLogins"
    href="https://api.yudu.com/Yudu/services/1.0/readerLogins/?reader=1234"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/subscriptions" name="subscriptions"
    href="https://api.yudu.com/Yudu/services/1.0/subscription/?reader=1234"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/reader/authorisedDevices" name="authorisedDevices"
    href="https://api.yudu.com/Yudu/services/1.0/readers/1234/authorisedDevices"
    type="application/vnd.yudu+xml"
  />
</reader>

```

### Finding an iDevice enabled publication

Suppose we wish to find all iDevice enabled publications:

``` xml
//Request
GET /Yudu/services/1.0/publications/?iDeviceEnabled=true&timestamp=1310378440 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 1234
Signature: 4321dcba

//Response
HTTP/1.1 200 OK
Date: Mon, 11 Jul 2011 10:00:40 GMT
Cache-Control: no-cache
Content-Type: application/vnd.yudu+xml
Content-Length: ...

<publications>
  <publication xmlns="http://schema.yudu.com" id="2345">
    <name>Example 1</name>
    <iDeviceEnabled>true</iDeviceEnabled>
    <androidEnabled>false</androidEnabled>
    <link rel="self" name="self"
      href="https://api.yudu.com/Yudu/services/1.0/publications/2345"
      type="application/vnd.yudu+xml"
    />
  </publication>
  <publication xmlns="http://schema.yudu.com" id="5432">
    <name>Example 2</name>
    <iDeviceEnabled>true</iDeviceEnabled>
    <androidEnabled>true</androidEnabled>
    <link rel="self" name="self"
      href="https://api.yudu.com/Yudu/services/1.0/publications/5432"
      type="application/vnd.yudu+xml"
    />
  </publication>
</publications>
```

### Finding a subscription

``` xml
//Request
GET /Yudu/services/1.0/subscriptions/?edition=5678&timestamp=1310378445 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 1234
Signature: acbd2143

//Response
HTTP/1.1 200 OK
Date: Mon, 11 Jul 2011 10:00:45 GMT
Cache-Control: no-cache
Content-Type: application/vnd.yudu+xml
Content-Length: ...

<subscriptions>
  <subscription xmlns="http://schema.yudu.com" id="9876">
    <title>example</title>
    <onDeviceTitle>examplesOnDevice</onDeviceTitle>
    <subscriptionType>flash_node</subscriptionType>
    <disabled>false</disabled>
    <defaultAuthorisedDeviceLimit>6</defaultAuthorisedDeviceLimit>
    <nodeId>456</nodeId>
    <link rel="self" name="self"
      href="https://api.yudu.com/Yudu/services/1.0/subscriptions/9876"
      type="application/vnd.yudu+xml"
    />
    <link rel="http://schema.yudu.com/readers" name="readers"
      href="https://api.yudu.com/Yudu/services/1.0/readers/?subscription=9876"
      type="application/vnd.yudu+xml"
    />
  </subscription>
  <subscription xmlns="http://schema.yudu.com" id="7623">
    <title>Universal Subscription</title>
    <onDeviceTitle>A Subscription</onDeviceTitle>
    <subscriptionType>universal_club</subscriptionType>
    <disabled>true</disabled>
    <defaultAuthorisedDeviceLimit>3</defaultAuthorisedDeviceLimit>
    <nodeId>1463</nodeId>
    <link rel="self" name="self"
      href="https://api.yudu.com/Yudu/services/1.0/subscriptions/7623"
      type="application/vnd.yudu+xml"
    />
    <link rel="http://schema.yudu.com/readers" name="readers"
      href="https://api.yudu.com/Yudu/services/1.0/readers/?subscription=7623"
      type="application/vnd.yudu+xml"
   />
  </subscription>
</editions>
```

### Creating a subscription period for an existing reader and subscription

Given the above examples, we can now create a subscription period for this reader and subscription:

``` xml
//Request
POST /Yudu/services/1.0/subscriptionPeriods/?timestamp=1310378450 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 1234
Signature: 5345

<subscriptionPeriod xmlns="http://schema.yudu.com">
  <reader id="1234"/>
  <subscription id="9876"/>
  <startDate>2014-02-11</startDate>
  <expiryDate>2013-06-13</expiryDate>
</subscriptionPeriod>

//Response
HTTP/1.1 201 CREATED
Date: Mon, 11 Jul 2011 10:00:50 GMT
Cache-Control: no-cache
Content-Type: application/vnd.yudu+xml
Content-Length: ...
Location: https://api.yudu.com/Yudu/services/1.0/subscriptionPeriods/9154
```

And if we get the subscriptionPeriod to find out what we've got:

``` xml
//Request
GET /Yudu/services/1.0/subscriptionPeriods/9154?timestamp=1310378460 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 1234
Signature: ab3ab451

//Response
HTTP/1.1 200 OK
Date: Mon, 11 Jul 2011 10:01:00 GMT
Cache-Control: no-cache
Content-Type: application/vnd.yudu+xml
Content-Length: ...

<subscriptionPeriod xmlns="http://schema.yudu.com" id="9154">
  <reader id="1234"/>
  <subscription id="9876"/>
  <startDate>2014-02-11</startDate>
  <expiryDate>2013-06-13</expiryDate>
  <link rel="self" name="self"
    href="https://api.yudu.com /Yudu/services/1.0/subscriptionPeriods/1"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/reader" name="reader"
    href="https://api.yudu.com /Yudu/services/1.0/readers/1234"
    type="application/vnd.yudu+xml"
  />
  <link rel="http://schema.yudu.com/subscription" name="subscription"
    href="https://api.yudu.com /Yudu/services/1.0/subscriptions/9876"
    type="application/vnd.yudu+xml"
  />
</subscriptionPeriod>
```

### Resetting the authorised devices for a reader

Suppose a user has logged in from too many different devices and you would like to reset their authorised devices so that they can log in from a new collection of devices. This is achieved by deleting all authorised devices associated with the reader:

``` xml
//Request
DELETE /Yudu/services/1.0/readers/1234/authorisedComputers/?timestamp=1310378470 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 2345
Signature: 5647

//Response
HTTP/1.1 204 NO CONTENT
Date: Mon, 11 Jul 2011 10:01:10 GMT
```

## Authenticating a reader

Given a the username and password for a reader we can check whether such a reader exists and whether the password submitted by the reader is correct.

First find a reader by username (note that usernames are unique at or below any given node).

``` xml
//Request
GET /Yudu/services/1.0/readers/?username=aUsername&timestamp=1310378475 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 1234
Signature: 4321dcba

//Response
HTTP/1.1 200 OK
Date: Mon, 11 Jul 2011 10:01:15 GMT
Cache-Control: no-cache
Content-Type: application/vnd.yudu+xml
Content-Length: ...

<readers>
  <reader xmlns="http://schema.yudu.com" id="1234">
    <username>example</username>
    <emailAddress>user@example.com</emailAddress>
    <firstName>Example</firstName>
    <lastName>User</lastName>
    <nodeId>1234</nodeId>
    <link rel="self" name="self"
      href="https://api.yudu.com/Yudu/services/1.0/readers/1234"
      type="application/vnd.yudu+xml"
    />
    <link rel="http://schema.yudu.com/reader/authentication" name="authentication"
      href="https://api.yudu.com/Yudu/services/1.0/readers/1234/authentication"
      type="application/vnd.yudu+xml"
    />
  </reader>
</readers>
```

Then send an authentication request with the submitted password:

``` xml
//Request
PUT /Yudu/services/1.0/readers/?username=aUsername&timestamp=1310378475 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: 1234
Signature: 12bc431

<authentication xmlns="http://schema.yudu.com">
  <password>userPassword</password>
</authentication>

//Response
HTTP/1.1 200 OK
Date: Mon, 11 Jul 2011 10:01:20 GMT
Cache-Control: no-cache
Content-Type: application/vnd.yudu+xml
Content-Length: ...

<authentication xmlns="http://schema.yudu.com">
  <authenticated>false</authenticated>
</authentication>
```
