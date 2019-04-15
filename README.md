Yudu Publisher REST API v2.0
============================

Looking for v1.0? The documentation is still available [here](https://github.com/yudugit/rest-api-documentation/tree/v1.0).

See the [changelog](CHANGELOG.md) for major changes since v1.0.

## Table of Contents

- [Introduction](#introduction)
  - [Overview](#overview)
  - [Terminology](#terminology)
  - [Overview](#overview-1)
  - [Sample code](#sample-code)
- [Outline](#outline)
  - [Resources](#resources)
  - [Verbs](#verbs)
  - [Relations](#relations)
  - [URI Summary](#uri-summary)
- [Resources](#resources-1)
  - [Service Description](#service-description)
  - [Reader](#reader)
  - [Edition](#edition)
  - [Permission](#permission)
  - [Subscription](#subscription)
  - [Subscription Period](#subscription-period)
  - [Reader Login](#reader-login)
  - [Authorised Device](#authorised-device)
  - [Authentication](#authentication)
  - [Web Edition SSO Tokens](#web-edition-sso-tokens)
  - [Targeted Notifications](#targeted-notifications)
- [Technical Details](#technical-details)
  - [Request Authentication](#request-authentication)
  - [Exceptions](#exceptions)
  - [Request Media Types](#a-word-about-request-media-types)
  - [Pagination](#pagination)
  - [Dates](#dates)
  - [Booleans](#booleans)
  - [Enumerations](#enumerations)
- [Example Sessions](#example-sessions)
  - [Creating A New Reader](#creating-a-new-reader)
  - [Finding An Edition](#finding-an-edition)
  - [Creating And Updating A Permission](#creating-and-updating-a-permission)
  - [Updating A Reader](#updating-a-reader)
  - [Finding All iDevice Enabled Publications](#finding-all-idevice-enabled-publications)
  - [Finding A Subscription](#finding-a-subscription)
  - [Creating A Subscription Period](#creating-a-subscription-period)
  - [Resetting Authorised Devices](#resetting-authorised-devices)
  - [Authenticating A Reader](#authenticating-a-reader)

## Introduction

This Document outlines the web service interface provided by YUDU to enable management of readers, purchases and subscriptions for digital editions.

Access to the service requires a Yudu Publisher account with the "REST API" permission and a valid API token created through the Yudu Publisher interface.

### Overview

The Yudu API uses the "Representational state transfer" (REST) architectural style. In particular, it applies the "hypermedia as the engine of application state" ([HATEOAS](http://en.wikipedia.org/wiki/HATEOAS)) principle in the design of the resources. If you are not already familiar with these principles then we recommend reading [REST in Practice](http://restinpractice.com/book/) as an introduction before diving further into the Yudu API.

### Terminology

The following terminology is used in this document:

- **Reader** - An end user that will be reading your digital editions
- **Edition** - A Yudu digital edition
- **Permission** - Refers to the granting of access to one edition for one reader
- **Reader login** - A particular instance of a reader accessing an edition
- **Publication** - A Yudu publication (also known as a "group")
- **Subscription** - A Yudu subscription
- **Subscription period** - Refers to the granting of access to a subscription for a time period
- **Authorised device** - A single device which has been used by a reader to access an edition
- **Web Edition SSO token** - A Single Sign-On (SSO) token valid for authentication for some set of (Web) Editions
- **Node** - The Yudu Publisher system is arranged into a hierarchy of nodes. For most users you won't need to worry about the node ID of your Readers, however if you would like to place them at different levels within your part of the hierarchy you can by specifying it.

### Overview

This service is arranged into **resources**, such as readers and their permissions. Each resource can be operated on by using different **HTTP methods** or **verbs** such as such as **GET**, **POST**, **PUT**, **DELETE** and **OPTIONS**.

### Sample code

All code samples can be found in the [examples](examples) directory and each is accompanied by a README.md file which contains the documentation.

Please note that these examples generally do not represent best practices for implementing clients of the API. For example, as noted below, rather than using the URIs as described, you are encouraged to make use of the hypermedia present in the resources to navigate the API, decoupling your implementation from ours. Unless otherwise stated, these examples should be treated as proof-of-concepts only, and not as applications ready for production use.

#### [PHP Client](examples/phpClient)

A sample PHP application that shows the use of the API. In particular, this example is not extensively tested and does not handle every possible error case in a suitable manner for production use.

#### [Java Client](examples/javaClient)

A sample GUI java application that can be used to build and send requests to our service.

#### [.NET Client](examples/dotNetClient)

A sample .NET console application that demonstrates how to build and send requests to our service.

#### [Ruby Command Line Tool](examples/rubyCommandLineTool)

A basic ruby command line tool to calculate the correct Base64 encoded HMAC SHA256 hash for any string and shared secret (see [Request Authentication](#request-authentication)). This can be used to check that your signing method is creating the correct signature.

## Outline

### Resources

Resources are the objects you interact with through the service. Each resource is accessible by a unique URI and supports a number of operations (see [Verbs](#verbs)). Examples of resources in this service are Readers, Editions and Subscriptions.

As well as URIs for individual resources, the API defines URIs for *lists* of resources. These URIs can be used to create new resources by appending them to the list, or search for resources with certain properties. We will often refer to these lists of resources as resources for convenience.

### Verbs

*HTTP Methods* or *Verbs* are the actions which can be used on each resource. There are 5 verbs supported by this API:

- **GET** - Retrieve the current representation of a resource.
- **POST** - Create a new resource.
- **PUT** - Update an existing resource.
- **DELETE** - Delete an existing resource.
- **OPTIONS** - Get a list of verbs which are supported by the given resource.

### Relations

*Relations* or *Link Relations* provide a way to navigate this service. Each XML representation of a resource will contain a number of *links* which reference other URIs. These URIs indicate the logical next steps when using this system.

Resource representations returned by this service may contain a `links` element which contains a list of `link` elements detailing a link to another resource or list of resources. They are represented in XML as follows:

``` xml
  <link rel="http://schema.yudu.com/editions" name="editions"
    href="https://api.yudu.com/Yudu/services/2.0/editions/?reader=1234"
    type="application/vnd.yudu+xml"/>
```

The attributes of the `link` element are:

- `rel` - The relation. This identifies the resource being accessed as documented for each resource in [Resources](#resources-1).
- `name` - The name of the relation. This is often the name of the linked resource is too but can convey different information, such as in links used for pagination or **Get** requests to resource lists.
- `href` - The URI of the linked resource.
- `type` - The content type of the representation of the resource.
 
In order to determine which verbs can be used to interact with a resource, it is recommended to make an **OPTIONS** request to the URI given in the link relation. This will return an `Allow` header containing a comma-separated list of allowed verbs.

### URI Summary

The following table summarises all the available resource URIs, and the effect of each verb on them. Each of them is relative to the base URI for our API: `https://api.yudu.com/Yudu/services/2.0`.

| Resource                                              | GET                                                 | POST                                  | PUT                               | DELETE                                      |
| ----------------------------------------------------- | --------------------------------------------------- | ------------------------------------- | --------------------------------- | ------------------------------------------- |
| [/](#service-description)                             | Returns a list of links to the other available URIs | N/A                                   | N/A                               | N/A                                         |
| [/readers/](#reader)                                 | Returns a list of readers                           | Creates a new reader                  | N/A                               | N/A                                         |
| [/readers/{id}](#reader)                             | Returns the details of a single reader              | N/A                                   | Updates a reader                  | Deletes a reader                            |
| [/editions/](#edition)                               | Gets a list of all editions                         | Creates a new edition                 | N/A                               | N/A                                         |
| [/editions/{id}](#edition)                           | Gets the details of a single edition                | N/A                                   | Updates an edition                | Deletes an edition                          |
| [/permissions/](#permission)                         | Lists all edition permissions by readers            | Creates a new permission for a reader | N/A                               | N/A                                         |
| [/permissions/{id}](#permission)                     | Gets the details of a single permission             | N/A                                   | Updates a permission              | Removes an existing permission              |
| [/readerLogins/](#reader-login)                      | Gets a list of all reader logins                    | N/A                                   | N/A                               | N/A                                         |
| [/readerLogins/{id}](#reader-login)                  | Gets the details of a single reader login           | N/A                                   | N/A                               | N/A                                         |
| [/publications/](#publication)                       | Gets a list of all publications                     | N/A                                   | N/A                               | N/A                                         |
| [/publications/{id}](#publication)                   | Gets the details of a single publication            | N/A                                   | N/A                               | N/A                                         |
| [/subscriptions/](#subscription)                     | Gets a list of subscriptions                        | N/A                                   | N/A                               | N/A                                         |
| [/subscriptions/{id}](#subscription)                 | Gets the details of a single subscription           | N/A                                   | N/A                               | N/A                                         |
| [/subscriptionPeriods/](#subscription-period)        | Gets a list of subscription periods                 | N/A                                   | N/A                               | N/A                                         |
| [/subscriptionPeriods/{id}](#subscription-period)    | Gets the details of a single subscription period    | Creates a new subscription period     | Updates a subscription period     | Removes an existing subscription period     |
| [/readers/{id}/authorisedDevices](#authorised-device) | N/A                                                 | N/A                                   | N/A                               | Removes all authorised devices for a reader |
| [/readers/{id}/authentication](#authentication)       | N/A                                                 | N/A                                   | Authenticates a reader's password | N/A                                         |
| [/targetedNotifications](#targeted-notifications)     | N/A                                                 | Sends a targeted notification         | N/A                               | N/A                                         |

## Resources

This section describes the various resources accessible within this API, their XML representations, the verbs that can be used to interact with them, and the relations they define. All unqualified URIs given throughout this section are relative to the base URI for our API: `https://api.yudu.com/Yudu/services/2.0`.

The XML representations of the resources have some common features:
- Resource elements contains an `id` attribute which corresponds to the ID in the URI for the resource.
- Resource elements contains a `links` element which contains a list of `link` elements defining the relations for the resource.
 
The XML representations of the lists of resources also have some common features:
- The root element of a list of resources representation contains "pagination" attributes. See [Pagination](#pagination) for details.
- The root element contains a `{resourceName}List` element which itself contains a list of resource representations.
- The root element contains a `links` element which contains a list of `link` elements for navigating the list if it has been paginated.
 
Further, the root element of any XML returned from or sent to the server must contain the namespace attribute: `xmlns="http://schema.yudu.com"`

In the XML descriptions of each resource the `link` elements within the `links` elements have been omitted for brevity. Similarly the returned resource representations within the `{resourceName}List` elements in a list representation have been omitted.

### Service Description

While not technically a resource this endpoint is the starting point for any interaction with the API. It is assumed that a user of the API does not know how to construct the URIs for any resource or the IDs or URIs of any existing resource. Instead a user starts at the service description and follows links to navigate the resource.

#### XML Representation

The service description is represented in XML as a root `service` tag with a single `links` tag containing the links for all the *lists* of resources.

``` xml
<service xmlns="http://schema.yudu.com">
    <links>
      ⋮ // some link elements
    </links>
</service>
```

#### Supported Verbs

| URI | Relation | Verbs   |
| --- | -------- | ------- |
| `/` | N/A      | **GET** |

##### GET

A **GET** request returns the XML representation of the service: a list of links to the other available URIs in the service.


### Reader

The reader corresponds to a "Subscriber" in Yudu Publisher.

#### XML Representation

##### Single Reader

``` xml
<reader xmlns="http://schema.yudu.com" id="1234">
  <username>example</username>
  <emailAddress>user@example.com</emailAddress>
  <firstName>Example</firstName>
  <lastName>User</lastName>
  <nodeId>1234</nodeId>
  <password>userPassword</password>
  <authorisedDeviceLimit>3</authorisedDeviceLimit>
  <links>
    ⋮ // some link elements
  </links>
</reader>
```

**Note:** The password field can be included by the user to update the password but will **not** be returned by the service.

##### Reader List

``` xml
<readers xmlns="http://schema.yudu.com" limit="100" offset="0" total="1293" truncated="true">
  <readerList>
    ⋮ // some reader elements
  </readerList>
  <links>
    ⋮ // some link elements
  </links>
</readers>
```

#### <a name="reader-permissible-fields"></a>Permissible Fields

| Element / Attribute     | PUT       | POST      |
| ----------------------- | --------- | --------- |
| `id`                    | Required  | Forbidden |
| `username`              | Allowed   | Required  |
| `emailAddress`          | Allowed   | Required  |
| `firstName`             | Allowed   | Required  |
| `lastName`              | Allowed   | Required  |
| `nodeId`                | Allowed   | Allowed   |
| `password`              | Allowed   | Required  |
| `authorisedDeviceLimit` | Allowed   | Allowed   |
| `links`                 | Forbidden | Forbidden |

#### Sortable Fields

Readers can be sorted by the following fields (see [Pagination](#pagination) for details):

- `id`
- `node`
- `emailAddress`
- `username`
- `firstName`
- `lastName`

#### Reader List

| URI         | Relation                         | Verbs             |
| ----------- | -------------------------------- | ----------------- |
| `/readers/` | `http://schema.yudu.com/readers` | **GET**, **POST** |

##### GET

A **GET** request returns the XML representation of a list of readers, optionally filtered using the following query string parameters, as well as the pagination parameters described in [Pagination](#pagination).

| Filter | Type | Description |
| ------ | ---- | ----------- |
| **emailAddress** | String | Filter by email address *prefix* |
| **username** | String | Filter by username *prefix* |
| **firstName** | String | Filter by given name *prefix* |
| **lastName** | String | Filter by family name *prefix* |
| **nodeId** | Integer | Return only readers created at the given node ID |
| **subscription** | Integer | Return only readers subscribed to the subscription with the given ID |

##### POST

A **POST** request creates a new reader. The request body must contain the XML representation of a reader with the required fields as detailed in [Permissible Fields](#reader-permissible-fields).

A successful **POST** will result in a **201 CREATED** response with a `Location` header specifying the URI of the newly created resource and the response body will contain the XML representation of the resource (including the `id` and `links`).

#### Single Reader

| URI             | Relation                        | Verbs                         |
| --------------- | ------------------------------- | ----------------------------- |
| `/readers/{id}` | `http://schema.yudu.com/reader` | **GET**, **PUT**, **DELETE**  |

##### GET

A **GET** request returns the XML representation of the reader. Note that any fields which do not have a value may not be included in the XML representation.

##### PUT

A **PUT** request updates an existing reader. The request body must contain the XML representation of a reader with the required fields as detailed in [Permissible Fields](#reader-permissible-fields). Of the fields marked "Allowed", only those which you wish to update should be included.

##### DELETE

A **DELETE** request deletes an existing reader.



### Edition

The edition corresponds to an "Edition" in Yudu Publisher.

#### XML Representation

##### Single Edition

``` xml
<edition xmlns="http://schema.yudu.com" id="1234">
  <name>My Edition</name>
  <publishedDate>2014-01-01T00:00:00Z</publishedDate>
  <flashLiveUrl>http://hosted.edition.domain/pathToEditionFlashUrl</flashLiveUrl>
  <webLiveUrl>http://hosted.edition.domain/pathToEditionWebUrl</webLiveUrl>
  <htmlLiveUrl>http://hosted.edition.domain/pathToEditionHtmlUrl</htmlLiveUrl>
  <onDeviceName>My Edition</onDeviceName>
  <drmEnabled>true</drmEnabled>
  <iosSaleOption>FREE</iosSaleOption>
  <iTunesConnectId>MyId</iTunesConnectId>
  <androidSaleOption>FREE</androidSaleOption>
  <enableSharingByEmail>true</enableSharingByEmail>
  <enablePrinting>true</enablePrinting>
  <googleAnalyticsTrackerId> UA-000000-0</googleAnalyticsTrackerId>
  <gaEditionDimensionIndex>1</gaEditionDimensionIndex>
  <gaReportNameInsteadOfNodeId>true</gaReportNameInsteadOfNodeId>
  <image_url>http://hosted.image.domain/pathToThumbnailUrl</image_url>
  <links>
    ⋮ // some link elements
  </links>
</edition>
```

##### Edition List

``` xml
<editions xmlns="http://schema.yudu.com" limit="100" offset="0" total="1293" truncated="true">
  <editionList>
    ⋮ // some edition elements
  </editionList>
  <links>
    ⋮ // some link elements
  </links>
</editions>
```

#### <a name="edition-permissible-fields"></a>Permissible Fields

| Element / Attribute           | PUT       | POST      |
| ----------------------------- | --------- | --------- |
| `name`                        | Required  | Allowed   |
| `onDeviceName`                | Required  | Allowed   |
| `shortName`                   | Required  | Allowed   |
| `drmEnabled`                  | Required  | Allowed   |
| `iosSaleOption`               | Required  | Allowed   |
| `iTunesConnectId`             | Allowed   | Allowed   |
| `androidSaleOption`           | Required  | Allowed   |
| `enableSharingByEmail`        | Required  | Allowed   |
| `enablePrinting`              | Required  | Allowed   |
| `googleAnalyticsTrackerId`    | Allowed   | Allowed   |
| `gaEditionDimensionIndex`     | Allowed   | Allowed   |
| `gaPlatformDimensionIndex`    | Allowed   | Allowed   |
| `gaReportNameInsteadOfNodeId` | Allowed   | Allowed   |
| `documentUrl`                 | Allowed   | Required  |
| `publicationNodeId`           | Forbidden | Required  |
| `stagedDaysLimit`             | Allowed   | Allowed   |
| `stagedViewsLimit`            | Allowed   | Allowed   |
| `pageBillingType`             | Allowed   | Allowed   |
| `targetState`                 | Required  | Allowed   |


#### Sortable Fields

Editions can be sorted by the following fields (see [Pagination](#pagination) for details):

- `id`
- `name`
- `publishedDate`

#### <a name="editions-edition-list"></a>Edition List

| URI          | Relation                          | Verbs   |
| ------------ | --------------------------------- | ------- |
| `/editions/` | `http://schema.yudu.com/editions` | **GET**, **POST** |

##### GET

A **GET** request returns the XML representation of a list of editions, optionally filtered using the following query string parameters, as well as the pagination parameters described in [Pagination](#pagination).

| Filter | Type | Description |
| ------ | ---- | ----------- |
| **name** | String | Filter by edition name *prefix* |
| **subscription** | Integer | Return only editions shipped to the subscription with the given ID |
| **publishedDate\_after** | [Date](#dates) |  Return only editions with an official publication date *after* the given date |
| **publishedDate\_before** | [Date](#dates) | Return only editions with an official publication date *before* the given date |
| **flashPublished** | [Boolean](#booleans) | Return only editions which are published (or not published) on the flash platform |
| **iOSPublished** | [Boolean](#booleans) | Return only editions which are published (or not published) on the iOS platform |
| **androidPublished** | [Boolean](#booleans) | Return only editions which are published (or not published) on the android platform
| **htmlPublished** | [Boolean](#booleans) | Return only editions which are published (or not published) on the HTML5 platform |
| **webPublished** | [Boolean](#booleans) | Return only editions which are published (or not published) on the combined web platform |

##### POST

A **POST** request creates a new edition. The request body must contain the XML representation of an EditionState with the required fields as detailed in [Permissible Fields](#edition-permissible-fields).

#### Single Edition

| URI              | Relation                         | Verbs   |
| ---------------- | -------------------------------- | ------- |
| `/editions/{id}` | `http://schema.yudu.com/edition` | **GET**, **PUT** |

##### GET

A **GET** request returns the XML representation of the edition. Note that any fields which do not have a value may not be included in the XML representation.

##### PUT

A **PUT** request updates an existing edition. The request body must contain the XML representation of an EditionState with the required fields as detailed in [Permissible Fields](#edition-permissible-fields). Of the fields marked "Allowed", any you do not include will be set to null.

##### XML Representation of an EditionState
``` xml
<editionState>
  <name>My Edition</name>
  <onDeviceName>My Edition</onDeviceName>
  <shortName>MyEdition</shortName>
  <targetState>
    <web>PREVIEW</web>
  </targetState>
  <drmEnabled>true</drmEnabled>
  <iosSaleOption>FREE</iosSaleOption>
  <androidSaleOption>FREE</androidSaleOption>
  <enableSharingByEmail>true</enableSharingByEmail>
  <enablePrinting>true</enablePrinting>
</editionState>
```

##### TargetState

A TargetState specifies the publish state the edition will get to for each platform.

Platforms are:  
web  
ios  
android

Options are:  
PREVIEW  
LIVE  
UNPUBLISH  
UNPREVIEW  
UNAVAILABLE

##### PageBillingType

A PageBillingType needs to be specified when a publish request is sent. This decides how the pages published are billed.

Options are:  
UNBILLED  
PLATFORM  
UNIVERSAL

Using the PLATFORM option will use the billing type specific to each platform being published.

##### DELETE

A **DELETE** request deletes an existing edition.

### Permission

The permission corresponds to an "Edition Permission" in Yudu Publisher.

#### XML Representation

##### Single Permission

``` xml
<permission xmlns="http://schema.yudu.com" id="1234">
  <reader id="567"/>
  <edition id="890"/>
  <creationDate>2014-01-01T00:00:00Z</creationDate>
  <expiryDate>2014-06-01T00:00:00Z</expiryDate>
  <links>
    ⋮ // some link elements
  </links>
</permission>
```

##### Permission List

``` xml
<permissions xmlns="http://schema.yudu.com" limit="100" offset="0" total="1293" truncated="true">
  <permissionList>
    ⋮ // some permission elements
  </permissionList>
  <links>
    ⋮ // some link elements
  </links>
</permissions>
```

#### <a name="permission-permissible-fields"></a>Permissible Fields

| Element / Attribute     | PUT       | POST      |
| ----------------------- | --------- | --------- |
| `id`                    | Required  | Forbidden |
| `reader`                | Forbidden | Required  |
| `edition`               | Forbidden | Required  |
| `creationDate`          | Forbidden | Forbidden |
| `expiryDate`            | Allowed   | Allowed   |
| `links`                 | Forbidden | Forbidden |

#### Sortable Fields

Permissions can be sorted by the following fields (see [Pagination](#pagination) for details):

- `id`
- `reader`
- `edition`
- `creationDate`
- `expiryDate`

#### Permission List

| URI             | Relation                             | Verbs             |
| --------------- | ------------------------------------ | ----------------- |
| `/permissions/` | `http://schema.yudu.com/permissions` | **GET**, **POST** |

##### GET

A **GET** request returns the XML representation of a list of permissions, optionally filtered using the following query string parameters, as well as the pagination parameters described in [Pagination](#pagination).

| Filter | Type | Description |
| ------ | ---- | ----------- |
| **reader** | Integer | Return only permissions for the reader with then given ID |
| **edition** | Integer | Return only permissions for the edition with then given ID |
| **creationDate\_after** | [Date](#dates) |  Return only permissions with a creation date *after* the given date |
| **creationDate\_before** | [Date](#dates) | Return only permissions with a creation date *before* the given date |
| **expiry\_after** | [Date](#dates) |  Return only permissions with an expiry date *after* the given date |
| **expiry\_before** | [Date](#dates) | Return only permissions with an expiry date *before* the given date |

##### POST

A **POST** request creates a new permission. The request body must contain the XML representation of a permission with the required fields as detailed in [Permissible Fields](#permission-permissible-fields).

A successful **POST** will result in a **201 CREATED** response with a `Location` header specifying the URI of the newly created resource and the response body will contain the XML representation of the resource (including the `id` and `links`).

#### Single Permission

| URI                 | Relation                            | Verbs                         |
| ------------------- | ----------------------------------- | ----------------------------- |
| `/permissions/{id}` | `http://schema.yudu.com/permission` | **GET**, **PUT**, **DELETE**  |

##### GET

A **GET** request returns the XML representation of the permission. Note that any fields which do not have a value may not be included in the XML representation.

##### PUT

A **PUT** request updates an existing permission. The request body must contain the XML representation of a permission with the required fields as detailed in [Permissible Fields](#permission-permissible-fields).

##### DELETE

A **DELETE** request deletes an existing permission.


### Subscription

The subscription corresponds to a "Subscription" in Yudu Publisher.

#### XML Representation

##### Single Subscription

``` xml
<subscription xmlns="http://schema.yudu.com" id="1234">
  <title>My Subscription</title>
  <onDeviceTitle>On Device Subscription Name</onDeviceTitle>
  <subscriptionType>flash_node</subscriptionType>
  <disabled>false</disabled>
  <defaultAuthorisedDeviceLimit>6</defaultAuthorisedDeviceLimit>
  <nodeId>567</nodeId>
  <links>
    ⋮ // some link elements
  </links>
</subscription>
```

##### Subscription List

``` xml
<subscriptions xmlns="http://schema.yudu.com" limit="100" offset="0" total="1293" truncated="true">
  <subscriptionList>
    ⋮ // some subscription elements
  </subscriptionList>
  <links>
    ⋮ // some link elements
  </links>
</subscriptions>
```
#### Sortable Fields

Subscriptions can be sorted by the following fields (see [Pagination](#pagination) for details):

- `id`
- `title`
- `onDeviceTitle`
- `disabled`
- `subscriptionType`
- `node`

#### Subscription List

| URI               | Relation                               | Verbs   |
| ----------------- | -------------------------------------- | ------- |
| `/subscriptions/` | `http://schema.yudu.com/subscriptions` | **GET** |

##### GET

A **GET** request returns the XML representation of a list of subscriptions, optionally filtered using the following query string parameters, as well as the pagination parameters described in [Pagination](#pagination).

| Filter | Type | Description |
| ------ | ---- | ----------- |
| **title** | String | Filter by subscription title *prefix* |
| **onDeviceTitle** | String | Filter by on device title *prefix* |
| **disabled** | [Boolean](#booleans) | Return only subscriptions which are disable (or not disabled) |
| **subscriptionType** | [SubscriptionType](#subscriptiontype) | Filter by the subscription type |
| **node** | Integer |  Return only subscriptions at a particular node |
| **reader** | Integer | Return only subscriptions to which the reader with the given ID is subscribed |
| **edition** | Integer | Return only subscriptions to which the editions with the given ID is shipped |

#### Single Subscription

| URI                   | Relation                              | Verbs   |
| --------------------- | ------------------------------------- | ------- |
| `/subscriptions/{id}` | `http://schema.yudu.com/subscription` | **GET** |

##### GET

A **GET** request returns the XML representation of the subscription. Note that any fields which do not have a value may not be included in the XML representation.


### Subscription Period

The subscription period corresponds to a "Subscription Period" in Yudu Publisher.

#### XML Representation

##### Single Subscription Period

``` xml
<subscriptionPeriod xmlns="http://schema.yudu.com" id="1234">
  <reader id="567"/>
  <subscription id="890"/>
  <startDate>2014-01-01T00:00:00Z</startDate>
  <expiryDate>2014-06-01T00:00:00Z</expiryDate>
  <links>
    ⋮ // some link elements
  </links>
</subscriptionPeriod>
```

##### Subscription Period List

``` xml
<subscriptionPeriods xmlns="http://schema.yudu.com" limit="100" offset="0" total="1293" truncated="true">
  <subscriptionPeriodList>
    ⋮ // some subscriptionPeriod elements
  </subscriptionPeriodList>
  <links>
    ⋮ // some link elements
  </links>
</subscriptionPeriods>
```

#### <a name="subscription-period-permissible-fields"></a>Permissible Fields

| Element / Attribute     | PUT       | POST      |
| ----------------------- | --------- | --------- |
| `id`                    | Required  | Forbidden |
| `reader`                | Forbidden | Required  |
| `subscription`          | Forbidden | Required  |
| `startDate`             | Allowed   | Required  |
| `expiryDate`            | Allowed   | Allowed   |
| `links`                 | Forbidden | Forbidden |

#### Sortable Fields

Subscription periods can be sorted by the following fields (see [Pagination](#pagination) for details):

- `id`
- `reader`
- `subscription`
- `startDate`
- `expiryDate`

#### Subscription Period List

| URI                     | Relation                                     | Verbs             |
| ----------------------- | -------------------------------------------- | ----------------- |
| `/subscriptionPeriods/` | `http://schema.yudu.com/subscriptionPeriods` | **GET**, **POST** |

##### GET

A **GET** request returns the XML representation of a list of subscriptionPeriods, optionally filtered using the following query string parameters, as well as the pagination parameters described in [Pagination](#pagination).

| Filter | Type | Description |
| ------ | ---- | ----------- |
| **reader** | Integer | Return only subscription periods for the reader with then given ID |
| **subscription** | Integer | Return only subscription periods for the subscription with then given ID |
| **startDate\_after** | [Date](#dates) |  Return only subscription periods with a start date date *after* the given date |
| **startDate\_before** | [Date](#dates) | Return only subscription periods with a start date date *before* the given date |
| **expiry\_after** | [Date](#dates) |  Return only subscription periods with an expiry date *after* the given date |
| **expiry\_before** | [Date](#dates) | Return only subscription periods with an expiry date *before* the given date |

##### POST

A **POST** request creates a new subscription period. The request body must contain the XML representation of a subscription period with the required fields as detailed in [Permissible Fields](#subscription-period-permissible-fields).

A successful **POST** will result in a **201 CREATED** response with a `Location` header specifying the URI of the newly created resource and the repsonse body will contain the XML representation of the resource (including the `id` and `links`).

#### Single Subscription Period

| URI                         | Relation                                    | Verbs                         |
| --------------------------- | ------------------------------------------- | ----------------------------- |
| `/subscriptionPeriods/{id}` | `http://schema.yudu.com/subscriptionPeriod` | **GET**, **PUT**, **DELETE**  |

##### GET

A **GET** request returns the XML representation of the permission. Note that any fields which do not have a value may not be included in the XML representation.

##### PUT

A **PUT** request updates an existing subscription period. The request body must contain the XML representation of a subscription period with the required fields as detailed in [Permissible Fields](#subscription-period-permissible-fields).

##### DELETE

A **DELETE** request deletes an existing subscription period.


### Reader Login

The reader login represents a single login of a reader into an edition or app.

#### XML Representation

##### Single Reader Login

``` xml
<readerLogin xmlns="http://schema.yudu.com" id="1234">
  <loginDate>2014-01-01T00:00:00Z</loginDate>
  <reader id="567"/>
  <node id="890"/>
  <platform>flash</platform>
  <emailAddress>user@example.com</emailAddress>
  <links>
    ⋮ // some link elements
  </links>
</subscription>
```

##### Reader Login List

``` xml
<readerLogins xmlns="http://schema.yudu.com" limit="100" offset="0" total="1293" truncated="true">
  <readerLoginList>
    ⋮ // some readerLogin elements
  </readerLoginList>
  <links>
    ⋮ // some link elements
  </links>
</readerLogins>
```

#### Sortable Fields

Reader logins can be sorted by the following fields (see [Pagination](#pagination) for details):

- `id`
- `reader`
- `node`
- `loginDate`
- `platform`
- `emailAddress`

#### Reader Login List

| URI              | Relation                              | Verbs   |
| ---------------- | ------------------------------------- | ------- |
| `/readerLogins/` | `http://schema.yudu.com/readerLogins` | **GET** |

##### GET

A **GET** request returns the XML representation of a list of reader logins, optionally filtered using the following query string parameters, as well as the pagination parameters described in [Pagination](#pagination).

| Filter | Type | Description |
| ------ | ---- | ----------- |
| **reader** | Integer | Return only reader logins by the reader with the given ID |
| **node** | Integer |  Return only reader logins to the given node ID |
| **loginDate\_after** | [Date](#dates) |  Return only reader logins *after* the given date |
| **loginDate\_before** | [Date](#dates) | Return only reader logins *before* the given date |
| **platform** | [Platform](#platform) | Filter by the platform the reader logged in from |
| **emailAddress** | String | Filter by email address *prefix* |

#### Single Reader Login

| URI                  | Relation                             | Verbs   |
| -------------------- | ------------------------------------ | ------- |
| `/readerLogins/{id}` | `http://schema.yudu.com/readerLogin` | **GET** |

##### GET

A **GET** request returns the XML representation of the reader login. Note that any fields which do not have a value may not be included in the XML representation.


### Publication

The publication corresponds to a "Group" in Yudu Publisher.

#### XML Representation

##### Single Publication

``` xml
<publication xmlns="http://schema.yudu.com" id="1234">
  <name>A Publication</name>
  <iDeviceEnabled>true</iDeviceEnabled>
  <androidEnabled>false</androidEnabled>
  <links>
    ⋮ // some link elements
  </links>
</publication>
```

##### Publication List

``` xml
<publications xmlns="http://schema.yudu.com" limit="100" offset="0" total="1293" truncated="true">
  <publicationList>
    ⋮ // some publication elements
  </publicationList>
  <links>
    ⋮ // some link elements
  </links>
</publications>
```

#### Sortable Fields

Publications can be sorted by the following fields (see [Pagination](#pagination) for details):

- `id`
- `name`
- `iDeviceEnabled`
- `androidEnabled`

#### Publication List

| URI              | Relation                              | Verbs   |
| ---------------- | ------------------------------------- | ------- |
| `/publications/` | `http://schema.yudu.com/publications` | **GET** |

##### GET

A **GET** request returns the XML representation of a list of publications, optionally filtered using the following query string parameters, as well as the pagination parameters described in [Pagination](#pagination).

| Filter | Type | Description |
| ------ | ---- | ----------- |
| **name** | String | Filter by publication name *prefix* |
| **iDeviceEnabled** | [Boolean](#booleans) | Return only publications which are enabled (or disabled) on the *idevice* platform |
| **androidEnabled** | [Boolean](#booleans) | Return only publications which are enabled (or disabled) on the *android*  / *air* platform |

#### Single Publication

| URI                  | Relation                             | Verbs   |
| -------------------- | ------------------------------------ | ------- |
| `/publications/{id}` | `http://schema.yudu.com/publication` | **GET** |

##### GET

A **GET** request returns the XML representation of the publication. Note that any fields which do not have a value may not be included in the XML representation.


### Authorised Device

An authorised device represents a unique device which a reader has used to log in to an edition or app. Note that this resource is a subresource of reader so a reader ID is required in the URI.

#### XML Representation

Authorised devices have no XML representation as the only permitted verb is **DELETE** on the list resource.

#### Authorised Device List

| URI                                | Relation                                   | Verbs      |
| ---------------------------------- | ------------------------------------------ | ---------- |
| `/readers/{id}/authorisedDevices/` | `http://schema.yudu.com/authorisedDevices` | **DELETE** |

##### DELETE

A **DELETE** request removes all authorised devices from a reader. The purpose of this request is to effectively reset the "authorised device count" of a reader.


### Authentication

The authentication resource allows a user to authenticate a given reader's password. Note that this resource is a sub-resource of reader so a reader ID is required in the URI.

#### XML Representation

##### Single Authentication

``` xml
<authentication xmlns="http://schema.yudu.com">
  <password>userPassword</password>
  <authenticated>true</authenticated>
</authentication>
```

**Note:** The password field must be included by the user to authenticate a reader but will **not** be returned by the service.

##### <a name="authentication-permissible-fields"></a>Permissible Fields

| Element / Attribute     | PUT       |
| ----------------------- | --------- |
| `password`              | Required  |
| `authenticated`         | Forbidden |

#### Single Authentication

| URI                             | Relation                                | Verbs   |
| ------------------------------- | --------------------------------------- | ------- |
| `/readers/{id}/authentication/` | `http://schema.yudu.com/authentication` | **PUT** |

##### PUT

A **PUT** request authenticates a reader.. The request body must contain the XML representation of an authentication with the required fields as detailed in [Permissible Fields](#authentication-permissible-fields).

### Web Edition SSO Tokens

The token resource allows a third party to generate a short-lifetime SSO token that can be passed to a Web Edition for user authentication without a login dialog. Note that multiple tiers of authorisation are available, meaning this resource is additionally available as a sub-resource of other resources. It requires a unique User ID for whom to generate the token.

#### XML Representation

##### Authentication Token

``` xml
<authToken xmlns="http://schema.yudu.com">
    <key>uniqueUserIdentification</key>
    <tokenValue>0123456789abcdefghijklmnopqrstu</tokenValue>
    <validity>Single edition</validity>
</authToken>
```

##### <a name="token-permissible-fields"></a>Permissible Fields

| Element / Attribute     | POST      |
| ----------------------- | --------- |
| `key`                   | Required  |
| `tokenValue`            | Forbidden |
| `validity`              | Forbidden |

#### All Available Editions Token

| URI        | Relation                       | Verbs    |
| ---------- | ------------------------------ | -------- |
| `/token/`  | `http://schema.yudu.com/token` | **POST** |

##### POST

A **POST** request creates an authentication token for the specified User ID. The request body must contain the XML representation of an authentication with the required fields as detailed in [Permissible Fields](#token-permissible-fields). The response body will contain the generated token value, as well as a human-readable description of the authorisation level granted for the token.

This URI will generate tokens that will authenticate for any edition available in your [Edition List](#editions-edition-list).

#### Single Publication Token

| URI                                     | Relation                       | Verbs    |
| --------------------------------------- | ------------------------------ | -------- |
| `/publications/{publicationID}/token/`  | `http://schema.yudu.com/token` | **POST** |

##### POST

A **POST** request creates an authentication token for the specified User ID. The request body must contain the XML representation of an authentication with the required fields as detailed in [Permissible Fields](#token-permissible-fields). The response body will contain the generated token value, as well as a human-readable description of the authorisation level granted for the token.

This URI will generate tokens that will authenticate for all editions under the Publication specified in the URI.

#### Single Edition Token

| URI                             | Relation                       | Verbs    |
| ------------------------------- | ------------------------------ | -------- |
| `/editions/{editionID}/token/`  | `http://schema.yudu.com/token` | **POST** |

##### POST

A **POST** request creates an authentication token for the specified User ID. The request body must contain the XML representation of an authentication with the required fields as detailed in [Permissible Fields](#token-permissible-fields). The response body will contain the generated token value, as well as a human-readable description of the authorisation level granted for the token.

This URI will generate tokens that will only authenticate for the Edition specified in the URI.

#### Using a Token

Given a User ID, the token resources generate a token value that, when used in combination with the ID, will authenticate a reader.
Currently, these token values can only be passed directly to an edition by means of the edition's URL.
By inserting both the ID and the token value into the URL as query parameters, the edition can be authenticated without requiring interaction by the reader.

In order to improve the seamless experience for your users, when a token generated by this API is used to authenticate a reader, the server will automatically provide that reader with another token.
This new token will continue to authenticate the reader for future visits for the rest of that day.
This token will be stored in the browser's local storage and so will be available for any visit from the same device and browser.
However, should token details be provided in the URL your reader follows, these will take precedence over any locally-stored token.

Note that since these tokens have a limited lifetime, if a user does attempt to reuse a URL with a token after the token has expired, they may be presented with a login screen as other users would be.
If the intention is to provide users with a seamless experience, then fresh URLs may need to be generated for them frequently, and they should be alerted to the limited lifetime of the URLs thus generated.
A token-less URL may be intentionally provided to allow users to use locally-stored token details.

##### Token URL Query Parameters

To successfully authenticate an edition using the token details, the following query parameters should be specified:

| Query Parameter Name | Token Parameter Name | Description                                                    |
| -------------------- | -------------------- | -------------------------------------------------------------- |
| `yuduAuthId`         | `key`                | The unique User ID for whom the token was generated            |
| `yuduAuthToken`      | `tokenValue`         | The generated value of the token returned in the response body |

For example, if your edition URL is `http://hosted.edition.domain/path/to/edition/index.html` then the token above could be used by directing the user to the destination `http://hosted.edition.domain/path/to/edition/index.html?yuduAuthId=uniqueUserIdentification&yuduAuthToken=0123456789abcdefghijklmnopqrstu`.
A simple use-case could be as follows:

1. A reader clicks on a link on your webpage indicating they wish to view an edition.
2. Your server reacts to that request by:
    1. sending a request to this API for a token for that user
    2. retrieving the token value from the response
    3. inserting the token value and user ID into the edition's target URL
    4. returning a 303 redirect with the modified edition URI as the target
3. The reader's browser redirects to the edition and the edition uses the token to authenticate.

### Targeted Notifications
A targeted notification represents a notification to be sent to a specified list of Yudu subscribers and/or third-party subscribers, via Firebase and APNS.

The targeted notification resource is different to other resources within the REST API, in that it doesn't represent an object of some kind, and therefore cannot be specified by an `id`. In addition to this, it never contains `link` elements defining relations of the resource and there are no pagination options necessary.

In addition to the "REST API" account permission, the "Send Custom Notifications" permission is also required for sending targeted notifications.

#### XML Representation
The targeted notification resource is represented in XML with a `targetedNotification` root element.
``` xml
<targetedNotification xmlns="http://schema.yudu.com">
    <nodeId>1234</nodeId>
    <message>Notification body</message>
    <title>Notification title</title>
    <notificationPriority>DEFAULT</notificationPriority>
    <subscribers>
        <thirdPartySubscriberToken>abcdef</thirdPartySubscriberToken>
        <subscriberUsername>abcdef</subscriberUsername>
    </subscribers>
</targetedNotification>
```

#### Permissible Fields
| Element                | Description                                         | Type                                          | POST     |
| ---------------------- | --------------------------------------------------- | --------------------------------------------- | -------- |
| `nodeId`               | Publication node ID                                 | Integer                                       | Required |
| `message`              | The body of the notification                        | String                                        | Required |
| `title`                | The title of the notification                       | String                                        | Allowed  |
| `notificationPriority` | The priority of the notification                    | [NotificationPriority](#notificationpriority) | Allowed  |
| `subscribers`          | The list of subscribers to send the notification to | [Subscriber elements](#subscriber-elements)   | Required |

##### NotificationPriority
The `notificationPriority` enumeration represents the priority of the notification being sent. The permissible values are:

* `DEFAULT`
* `HIGH`

Please note that high priority notifications must be enabled at the publication for this to take effect. If no priority is specified or high priority notifications are not enabled, a notification with default priority will be sent.

##### Subscriber Elements
The `subscribers` element can contain multiple third party subscribers and/or Yudu subscribers, but must contain at minimum one of either.

| Subscriber Element          | Description                       | Type   |
| --------------------------- | --------------------------------- | ------ |
| `thirdPartySubscriberToken` | Third party subscriber identifier | String |
| `subscriberUsername`        | Yudu subscriber username          | String |


#### Supported Verbs
| URI                      | Relation                                      | Verbs |
| ------------------------ | --------------------------------------------- | ----- |
| `/targetedNotifications` | `http://schema.yudu.com/targetedNotification` | POST  |

A **POST** request sends a targeted notification to the specified list of subscribers, and returns an XML representation of the response.

#### Targeted Notification Response
A targeted notification response will be returned as an XML representation, providing information about the success of iOS push notifications and Firebase cloud messages, and reasons for failures.

##### XML Failure Response
``` xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<targetedNotificationResponse xmlns="http://schema.yudu.com">
    <firebaseResponse>
        <allSucceeded>false</allSucceeded>
        <responseMessage>
            Firebase certificate details are missing for this node.
            Please ensure the Firebase private key has been uploaded and selected,
            and the project ID supplied on the "editPublication.htm" page.
        </responseMessage>
    </firebaseResponse>
    <iOSResponse>
        <allSucceeded>false</allSucceeded>
        <responseMessage>
            Push notification certificate details are missing for this node.
            Please ensure the APNS security certificate has been uploaded and selected,
            and the password supplied on the "editPublication.htm" page.
        </responseMessage>
    </iOSResponse>
</targetedNotificationResponse>
```

##### XML Success Response
``` xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<targetedNotificationResponse xmlns="http://schema.yudu.com">
    <firebaseResponse>
        <allSucceeded>true</allSucceeded>
        <responseMessage>
            Successfully completed sending Firebase messages.
            There were 1 messages to send, and 1 sent successfully.
        </responseMessage>
    </firebaseResponse>
    <iOSResponse>
        <allSucceeded>true</allSucceeded>
        <responseMessage>
            Completed sending iOS push notifications.
            There were 1 messages to send, and 1 sent successfully.
        </responseMessage>
    </iOSResponse>
</targetedNotificationResponse>
```

## Technical Details

### Request Authentication

#### Overview

Each request must be accompanied by a two piece authentication scheme. To access the service a *key* and *shared secret* must be used. These can be obtained and managed through the Yudu Publisher interface. The *key* is used for identification and the *shared secret* is used to sign each request. Both the *key* and *signature* should be included in specific request headers.

#### Authentication Header

The API *key* must be supplied with each request as a request header called `Authentication`.

#### Timestamp Query Parameter

The query parameters in the URI of each request must include `timestamp` - a [unix epoch timestamp](http://en.wikipedia.org/wiki/Unix_time) (in seconds) of the request. The timestamp is checked on our server to protect against replay attacks. You must make sure your server time is set accurately otherwise your requests may be rejected.

#### Signature Header

The shared secret should be used as the signing token used to generate a base-64 encoded HMAC-SHA256 hash of a specific string.

The signed string is constructed from the following parts:

1. The HTTP method of the request, in upper case. (e.g. `PUT`)
2. The absolute path of the service URI (e.g. `/Yudu/services/2.0/permissions/`)
3. The query string of the URI, including a `timestamp` parameter as detailed [above](#timestamp-query-parameter), before they have been URL encoded, and sorted alphabetically (e.g. `?edition=123&reader=456&timestamp=1234567890` or `?emailAddress=user@example.com&timestamp=1234567890`)
4. The full body of the request if appropriate - for **PUT** and **POST** requests - including whitespace if the body includes whitespace (e.g. `<permission xmlns="http://schema.yudu.com" id="1234"><expiryDate>2014-06-01T00:00:00Z</expiryDate></permission>`)

##### Example 1:

When making a **GET** request to the following URI and query string:

`http://api.yudu.com/Yudu/services/2.0/permissions/?timestamp=1234567890&reader=1234`

you should generate the HMAC using the string

`GET/Yudu/services/1.0/permissions/?reader=1234&timestamp=123456789`

##### Example 2:

When making a **POST** request to the following URI and query string:

`http://api.yudu.com/Yudu/services/2.0/subscriptionPeriod/?timestamp=1234567890`

and with the post data:

``` xml
<subscriptionPeriod xmlns="http://schema.yudu.com">
  <reader id="1234"/>
  <subscription id="5678"/>
  <startDate>2014-01-01T00:00:00Z</startDate>
</subscriptionPeriod>
```

you should generate the HMAC using the string

```
POST/Yudu/services/1.0/permissions/?reader=1234&timestamp=123456789<subscriptionPeriod xmlns="http://schema.yudu.com">
  <reader id="1234"/>
  <subscription id="5678"/>
  <startDate>2014-01-01T00:00:00Z</startDate>
</subscriptionPeriod>
```

Note that the whitespace, including line breaks, is included in the signed string.

### Exceptions

Errors will be signified by the using the relevant HTTP Status Code. For example "405 Method Not Allowed" if you attempt to **PUT** to a resource that does not support it, or "400 Bad Request" if the request was not valid. Many errors will have a response body containing an XML document describing the error in detail.

#### XML Representation

``` xml
<error xmlns="http://schema.yudu.com">
  <code>VALIDATION_FAILURE</code>
  <detail>Validation failed: Username is already in use</detail>
  <validationFailures>
    <failure>
      <cause>DUPLICATE_USERNAME</cause>
      <field>username</field>
    </failure>
    <failure>
      <cause>NULL</cause>
      <field>password</field>
    </failure>
  </validationFailures>
</error>
```

All error representations will contain the `code` and `detail` elements. Only errors with the code `VALIDATION_FAILURE` will contain the `validationFailures` element.

#### Error Codes

In addition to standard HTTP status codes the service returns a descriptive code in the `code` element. The possible values are:

- **NOT_FOUND** - The resource could not be found.
- **DUPLICATE_ITEM** - The resource already exists.
- **AUTHENTICATION_FAILURE** - The authentication credentials were not correct.
- **VALIDATION_FAILURE** - The XML in the request body was invalid.
- **UNKNOWN_ERROR** - An unknown error occurred.
- **CLIENT_ERROR** - A bad request has been sent by the client.
- **SERVER_ERROR** - An unknown internal error occurred.

### A Word About Request Media Types

The media type of all communications with the server should be of the form `application/vnd.yudu+xml`. Thus this type should always be included in an appropriate `Accept` header, and when necessary, specified as the type of any POST data. Please see the [Example Sessions](#example-sessions) to see how this should affect the requests you send. If you encounter an HTTP 406 status code, or a "Not Acceptable" error message without any further details, please check you have included this header in your request.

### Pagination

The response to a **GET** request to a list of resources is paginated. By default the resulting XML contains at all the resources if there are less than 100 and truncated the results to 100 if there are more.

The root element of a list of resources representation contains the following pagination attributes:
  - `limit` - The number of resource representations returned in this request.
  - `offset` - The offset from the first resource in the list.
  - `total` - The total number of resources in the list.
  - `truncated` - A boolean value whose value is false if all editions in the list have been returned in this response and true otherwise.
 
It is possible to set `limit`, `offset` and `sort` query parameters in order to access other resources.

#### Limit

The `limit` query parameter can be set to change the number of resources returned. The `limit` parameter must be between 1 and 1000 inclusive.

#### Offset

The `offset` query parameter can be set to request resources starting at a particular offset from the start. The `offset` parameter cannot be less than 0. This allows the client to access all the resources available by making multiple requests with a different offset. If the offset value is greater than the total, no resources will be returned. If the sum of the offset and limit is greater than the total, less resources than the limit will be returned.

For example, we could make one request with `offset=0` to obtain the first 100 items and `offset=100` to obtain the next 100 items, and so on.

#### Sort

The `sort` query parameter allows the client to choose the order in which the resources are returned. The sort parameter value is an ordered comma separated list of attribute names concatenated with `_asc` or `_desc` to signify whether the results should be sorted by that attribute in ascending or descending order. Each resource defined above is accompanied by list of permissible sort attributes names.

For example, suppose we wanted to list editions sorted first by name (ascending) and then by official publication date (descending). That is, we wish for the editions be sorted by the name, and for any two editions with the same name will be sorted with the latest publication date first. In this case we would use the following query parameter:

`sort=name_asc,publishedDate_desc`

Note that the order of the attributes is important. We must include the `name_asc` part first to ensure that this is the primary sort criteria.

#### Links

The list representations contain a `links` element just like the singular resource representations. If the list is truncated several links may be available for navigating the paginated list.

The `next` link will be available if there are more resources accessible by *increasing* the `offset` parameter. The link relation is the list resource, the `name` is `next` and the `href` will have the same `limit` and `sort` parameters and an adjusted `offset` parameter to obtain the next resources.

The `previous` link will be available if there are more resources accessible by *decreasing* the `offset` parameter. The link relation is the list resource, the `name` is `previous` and the `href` will have the same `limit` and `sort` parameters and an adjusted `offset` parameter to obtain the previous resources. Note that since the offset cannot be less than 0, if offset is less than limit, following the previous link may result in some of the same resources being presented as are available on the current page.

### Dates

Date parameters are used throughout this API, both in XML and in the URI query strings. All dates must be given in [ISO 8601](http://en.wikipedia.org/wiki/ISO_8601) format.

### Booleans

Boolean parameters are used throughout this API, both in XML and in URI query strings.

- In XML documents the only strings permissible as boolean values are `true` and `false`.
- In XML returned by the server, the strings `true` and `false` will be used to specify boolean values.
- In query strings the following boolean pairs are accepted:
  - `true` and `false`
  - `t` and `f`
  - `yes` and `no`
  - `y` and `n`
  - `1` and `0`

### Enumerations

Certain fields of the resource representations are enumerations. That is, there are a set number of permissible string values for the field. Such enumerations and their values are listed here.

#### SubscriptionType

The `subscriptionType` enumeration represents the type of subscription which is chosen when the subscription is created in Yudu Publisher. The permissible values are as follows:

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

#### Platform

The `platform` enumeration represents a device or platform type. The permissible values are as follows:

- **flash** - a web based app (flash or HTML5)
- **air** - an air based app (Android or Desktop)
- **idevice** - an iOS based app
- **unknown** - platform could not be determined

## Example Sessions

### Creating A New Reader

We wish to create a new reader at the node "1234". Request the base URI, which provides links to the other resources:

```
//Request
GET /Yudu/services/2.0/?timestamp=1412586000 HTTP/1.1
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: YmYtVK8Se2GuFNlGNsRoiBT1WApfF85pPMVETI/FkFo=
```

``` xml
//Response
HTTP/1.1 200 OK
Date: Mon, 06 Oct 2014 10:00:00 GMT
Content-Type: application/vnd.yudu+xml

<service xmlns="http://schema.yudu.com">
  <links>
    <link rel="http://schema.yudu.com/permissions" name="permissions"
          href="https://api.yudu.com/Yudu/services/2.0/permissions"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/readerLogins" name="readerLogins"
          href="https://api.yudu.com/Yudu/services/2.0/readerLogins"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/subscriptions" name="subscriptions"
          href="https://api.yudu.com/Yudu/services/2.0/subscriptions"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/readers" name="readers"
          href="https://api.yudu.com/Yudu/services/2.0/readers"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/editions" name="editions"
          href="https://api.yudu.com/Yudu/services/2.0/editions"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/publications" name="publications"
          href="https://api.yudu.com/Yudu/services/2.0/publications"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/subscriptionPeriods" name="subscriptionPeriods"
          href="https://api.yudu.com/Yudu/services/2.0/subscriptionPeriods"
          type="application/vnd.yudu+xml"/>
  </links>
</service>
```

This tells us that there are seven links, with the relations defined as above.

We want to create a new reader, and we know from the link relations that we can do this by submitting a **POST** request containing a `reader` to a link of type `http://schema.yudu.com/readers`. We check this operation is permitted by submitting an **OPTIONS** request to the URI provided in the `readers` link.

```
//Request
OPTIONS /Yudu/services/2.0/readers?timestamp=1412586005 HTTP/1.1
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: 6XjiQUH2PTRDLXFtzRSGZQ20Fl7yOSOA2h2DA/V/jtw=
```

```
//Response
HTTP/1.1 204 No Content
Date: Mon, 06 Oct 2014 10:00:05 GMT
Allow: OPTIONS,POST,GET,HEAD
```

The `Allow` header tells us that the **POST** method is allowed for this resource so we submit a **POST** request containing a `reader` representation.

``` xml
//Request
POST /Yudu/services/2.0/readers?timestamp=1412586010 HTTP/1.1
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: YmYtVK8Se2GuFNlGNsRoiBT1WApfF85pPMVETI/FkFo=

<reader xmlns="http://schema.yudu.com">
  <username>example</username>
  <emailAddress>user@example.com</emailAddress>
  <firstName>Example</firstName>
  <lastName>User</lastName>
  <password>password</password>
  <nodeId>1234</nodeId>
</reader>
```

``` xml
//Response
HTTP/1.1 201 CREATED
Date: Mon, 11 Jul 2011 10:00:10 GMT
Content-Type: application/vnd.yudu+xml
Location: https://api.yudu.com/Yudu/services/1.0/readers/5678

<reader xmlns="http://schema.yudu.com" id="5678">
  <username>example</username>
  <emailAddress>user@example.com</emailAddress>
  <firstName>Example</firstName>
  <lastName>User</lastName>
  <authorisedDeviceLimit>3</authorisedDeviceLimit>
  <nodeId>1234</nodeId>
  <links>
    <link rel="http://schema.yudu.com/reader" name="self"
          href="https://api.yudu.com/Yudu/services/2.0/readers/5678"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/permissions" name="permissions"
          href="https://api.yudu.com/Yudu/services/2.0/permissions?reader=5678"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/readerLogins" name="readerLogins"
          href="https://api.yudu.com/Yudu/services/2.0/readerLogins?reader=5678"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/subscriptions" name="subscriptions"
          href="https://api.yudu.com/Yudu/services/2.0/subscriptions?reader=5678"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/authorisedDevices" name="authorisedDevices"
          href="https://api.yudu.com/Yudu/services/2.0/readers/5678/authorisedDevices"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/authentication" name="authentication"
          href="https://api.yudu.com/Yudu/services/2.0/readers/5678/authentication"
          type="application/vnd.yudu+xml"/>
  </links>
</reader>
```

The response has returned the location of our new reader object in the `Location` header and the representation of the resource.

### Finding An Edition

We wish to find all editions with a name starting with "Example". Make a **GET** request to the editions endpoint with the query parameter `name=Example`:

```
//Request
GET /Yudu/services/1.0/editions?name=Examp&timestamp=1412586015 HTTP/1.1
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: k9PUkF0Vy9nQPnd5Dm6zRsXTyseOdlF+3F3/Bm0XUqY=
```

``` xml
//Response
HTTP/1.1 200 OK
Date: Mon, 06 Oct 2014 10:00:15 GMT
Content-Type: application/vnd.yudu+xml

<editions xmlns="http://schema.yudu.com" limit="100" offset="0" total="2" truncated="false">
  <editionList>
    <edition id="5678">
      <name>Example 1</name>
      <publishedDate>2011-01-20T00:00:00Z</publishedDate>
      <links>
        <link rel="http://schema.yudu.com/edition" name="self"
              href="https://api.yudu.com/Yudu/services/2.0/editions/5678"
              type="application/vnd.yudu+xml"/>
        <link rel="http://schema.yudu.com/permissions" name="permissions"
              href="https://api.yudu.com/Yudu/services/2.0/permissions?edition=5678"
              type="application/vnd.yudu+xml"/>
        <link rel="http://schema.yudu.com/subscriptions" name="subscriptions"
              href="https://api.yudu.com/Yudu/services/2.0/subscriptions?edition=5678"
              type="application/vnd.yudu+xml"/>
      </links>
    </edition>
    <edition id="9012">
      <name>Example 2</name>
      <publishedDate>2011-01-27T12:24:00Z</publishedDate>
      <links>
        <link rel="http://schema.yudu.com/edition" name="self"
              href="https://api.yudu.com/Yudu/services/2.0/editions/9012"
              type="application/vnd.yudu+xml"/>
        <link rel="http://schema.yudu.com/permissions" name="permissions"
              href="https://api.yudu.com/Yudu/services/2.0/permissions?edition=9012"
              type="application/vnd.yudu+xml"/>
        <link rel="http://schema.yudu.com/subscriptions" name="subscriptions"
              href="https://api.yudu.com/Yudu/services/2.0/subscriptions?edition=9012"
              type="application/vnd.yudu+xml"/>
      </links>
    </edition>
  </editionList>
  <links/>
</editions>
```

The response body contains an edition list representation. Note that since the resulting list has not been truncated the `editions` element contains an empty `links` element as there are no further pages of results to navigate.

### Creating And Updating A Permission

Given the above examples, we can now create a permission for this reader and one of the editions we found:

``` xml
//Request
POST /Yudu/services/1.0/permissions?timestamp=1412586020 HTTP/1.1
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: Xmvv80WIub0NazhL8TV50h4wxWxHuUs9cVYeASnoTFE=

<permission xmlns="http://schema.yudu.com">
  <reader id="5678"/>
  <edition id="9012"/>
</permission>
```

``` xml
//Response
HTTP/1.1 201 CREATED
Date: Mon, 06 Oct 2014 10:00:20 GMT
Content-Type: application/vnd.yudu+xml
Location: https://api.yudu.com/Yudu/services/1.0/permissions/3456

<permission id="3456">
  <reader id="5678"/>
  <edition id="9012"/>
  <creationDate>2014-10-06T10:00:20Z</creationDate>
  <links>
    <link rel="http://schema.yudu.com/permission" name="self"
          href="https://api.yudu.com/Yudu/services/2.0/permissions/3456"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/reader" name="reader"
          href="https://api.yudu.com/Yudu/services/2.0/readers/5678"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/edition" name="edition"
          href="https://api.yudu.com/Yudu/services/2.0/editions/9012"
          type="application/vnd.yudu+xml"/>
  </links>
</permission>
```

Now we update the permission and add an expiry date:

``` xml
//Request
PUT /Yudu/services/1.0/permissions/3456?timestamp=1412586025 HTTP/1.1
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: 4fUApJWR72cBwYfB00AtcJht5OhwhpuW9QCNq9jFePY=

<permission xmlns="http://schema.yudu.com" id="3456">
  <expiryDate>2015-06-01T00:00:00Z</expiryDate>
</permission>
```

``` xml
//Response
HTTP/1.1 200 OK
Date: Mon, 06 Oct 2014 10:00:25 GMT
Content-Type: application/vnd.yudu+xml

<permission id="3456">
  <reader id="5678"/>
  <edition id="9012"/>
  <creationDate>2014-10-06T10:00:20Z</creationDate>
  <expiryDate>2015-06-01T00:00:00Z</expiryDate>
  <links>
    <link rel="http://schema.yudu.com/permission" name="self"
          href="https://api.yudu.com/Yudu/services/2.0/permissions/3456"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/reader" name="reader"
          href="https://api.yudu.com/Yudu/services/2.0/readers/5678"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/edition" name="edition"
          href="https://api.yudu.com/Yudu/services/2.0/editions/9012"
          type="application/vnd.yudu+xml"/>
  </links>
</permission>
```

### Updating A Reader

Suppose we need to change a reader's password:

``` xml
//Request
PUT /Yudu/services/1.0/readers/5678?timestamp=1412586030 HTTP/1.1
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: VN7B4U5Uv4X4Yx+2qt7WUzCLBbb7Ssejaf1XHOmtACI=

<reader xmlns="http://schema.yudu.com" id="5678">
  <password>newPassword</password>
</reader>
```

``` xml
//Response
HTTP/1.1 200 OK
Date: Mon, 06 Oct 2014 10:00:30 GMT
Content-Type: application/vnd.yudu+xml

<reader xmlns="http://schema.yudu.com" id="5678">
  <username>example</username>
  <emailAddress>user@example.com</emailAddress>
  <firstName>Example</firstName>
  <lastName>User</lastName>
  <authorisedDeviceLimit>3</authorisedDeviceLimit>
  <nodeId>1234</nodeId>
  <links>
    <link rel="http://schema.yudu.com/reader" name="self"
          href="https://api.yudu.com/Yudu/services/2.0/readers/5678"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/permissions" name="permissions"
          href="https://api.yudu.com/Yudu/services/2.0/permissions?reader=5678"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/readerLogins" name="readerLogins"
          href="https://api.yudu.com/Yudu/services/2.0/readerLogins?reader=5678"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/subscriptions" name="subscriptions"
          href="https://api.yudu.com/Yudu/services/2.0/subscriptions?reader=5678"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/authorisedDevices" name="authorisedDevices"
          href="https://api.yudu.com/Yudu/services/2.0/readers/5678/authorisedDevices"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/authentication" name="authentication"
          href="https://api.yudu.com/Yudu/services/2.0/readers/5678/authentication"
          type="application/vnd.yudu+xml"/>
  </links>
</reader>

```

### Finding All iDevice Enabled Publications

Suppose we wish to find all iDevice enabled publications. Suppose further that we wish to see 2 publications at a time and navigate through the whole list:

```
//Request
GET /Yudu/services/1.0/publications/?iDeviceEnabled=true&limit=2&timestamp=1412586035 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: qXRVLXasp6li50Z2WAsn9WSiWeDWz54seyFdisRYbew=
```

``` xml
//Response
HTTP/1.1 200 OK
Date: Mon, 06 Oct 2014 10:00:35 GMT
Content-Type: application/vnd.yudu+xml

<publications xmlns="http://schema.yudu.com" limit="2" offset="0" total="3" truncated="true">
  <publicationList>
    <publication id="2345">
      <name>Publication 1</name>
      <iDeviceEnabled>true</iDeviceEnabled>
      <androidEnabled>false</androidEnabled>
      <links>
        <link rel="self" name="self"
              href="https://api.yudu.com/Yudu/services/1.0/publications/2345"
              type="application/vnd.yudu+xml"/>
      </links>
    </publication>
    <publication id="6789">
      <name>Publication 2</name>
      <iDeviceEnabled>true</iDeviceEnabled>
      <androidEnabled>true</androidEnabled>
      <links>
        <link rel="self" name="self"
              href="https://api.yudu.com/Yudu/services/1.0/publications/6789"
              type="application/vnd.yudu+xml"/>
      </links>
    </publication>
  <publicationList>
  <links>
    <link rel="http://schema.yudu.com/publications" name="next"
          href="http://api.yudu.com/Yudu/services/2.0/publications?limit=2&amp;offset=2"
          type="application/vnd.yudu+xml"/>
  </links>
</publications>
```

We can see that there were 3 results and we are only seeing the first two, as per our `limit` parameter. Now we can see the next result by following the `next` link, which adds an `offset=2` parameter.

```
//Request
GET /Yudu/services/1.0/publications/?iDeviceEnabled=true&limit=2&offset=2&timestamp=1412586040 HTTP/1.1
Host: api.yudu.com
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: 5P4EF0iS/trLXUWZ0e6U+WSbfX3oBAy0YirfWOwx6KM=
```

``` xml
//Response
HTTP/1.1 200 OK
Date: Mon, 06 Oct 2014 10:00:40 GMT
Content-Type: application/vnd.yudu+xml

<publications xmlns="http://schema.yudu.com" limit="2" offset="2" total="3" truncated="true">
  <publicationList>
    <publicationid="4321">
      <name>Publication 3</name>
      <iDeviceEnabled>true</iDeviceEnabled>
      <androidEnabled>false</androidEnabled>
      <links>
        <link rel="self" name="self"
              href="https://api.yudu.com/Yudu/services/1.0/publications/4321"
              type="application/vnd.yudu+xml"/>
      </links>
    </publication>
  <publicationList>
  <links>
    <link rel="http://schema.yudu.com/publications" name="previous"
          href="http://api.yudu.com/Yudu/services/2.0/publications?limit=2&amp;offset=0"
          type="application/vnd.yudu+xml"/>
  </links>
</publications>
```

Now the service returns the final publication and the only link available is the `previous` link to take us back to an offset of 0.

### Finding A Subscription

Suppose we wish to find all subscriptions which contain the edition with id 5678. Furthermore, suppose we wish to order the results by subscriptionType, in descending order:

```
//Request
GET /Yudu/services/1.0/subscriptions/?edition=5678&sort=subscriptionType_desc&timestamp=1412586045 HTTP/1.1
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: ipNslQR4oH6lWhx9Kqr5pNKs7T8zg6TNzgifWnQtl8M=
```

``` xml
//Response
HTTP/1.1 200 OK
Date: Mon, 06 Oct 2014 10:00:45 GMT
Content-Type: application/vnd.yudu+xml

<subscriptions xmlns="http://schema.yudu.com" limit="100" offset="0" count="2" truncated="false">
  <subscriptionList>
    <subscription id="9876">
      <title>Universal Subscription</title>
      <onDeviceTitle>subscription on device</onDeviceTitle>
      <subscriptionType>universal_club</subscriptionType>
      <disabled>false</disabled>
      <defaultAuthorisedDeviceLimit>6</defaultAuthorisedDeviceLimit>
      <nodeId>1234</nodeId>
      <links>
        <link rel="http://schema.yudu.com/subscription" name="self"
              href="https://api.yudu.com/Yudu/services/2.0/subscriptions/9876"
              type="application/vnd.yudu+xml"/>
        <link rel="http://schema.yudu.com/readers" name="readers"
              href="https://api.yudu.com/Yudu/services/2.0/readers?subscription=9876"
              type="application/vnd.yudu+xml"/>
        <link rel="http://schema.yudu.com/editions" name="editions"
              href="https://api.yudu.com/Yudu/services/2.0/editions?subscription=9876"
              type="application/vnd.yudu+xml"/>
      </links>
    </subscription>
    <subscription id="5432">
      <title>Example Subscription</title>
      <onDeviceTitle>A Subscription</onDeviceTitle>
      <subscriptionType>flash_node</subscriptionType>
      <disabled>true</disabled>
      <defaultAuthorisedDeviceLimit>3</defaultAuthorisedDeviceLimit>
      <nodeId>1234</nodeId>
      <links>
        <link rel="http://schema.yudu.com/subscription" name="self"
              href="https://api.yudu.com/Yudu/services/2.0/subscriptions/5432"
              type="application/vnd.yudu+xml"/>
        <link rel="http://schema.yudu.com/readers" name="readers"
              href="https://api.yudu.com/Yudu/services/2.0/readers?subscription=5432"
              type="application/vnd.yudu+xml"/>
        <link rel="http://schema.yudu.com/editions" name="editions"
              href="https://api.yudu.com/Yudu/services/2.0/editions?subscription=5432"
              type="application/vnd.yudu+xml"/>
      </links>
    </subscription>
  </subscriptionList>
  <links/>
</subscriptions>
```

### Creating A Subscription Period

Given the above examples, we can now create a subscription period for a reader and subscription:

``` xml
//Request
POST /Yudu/services/1.0/subscriptionPeriods/?timestamp=1412586050 HTTP/1.1
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: azckQ5GtqtDQe0EW6v5EBsC79QMXg9e/k1yezUKPFjA=

<subscriptionPeriod xmlns="http://schema.yudu.com">
  <reader id="1234"/>
  <subscription id="9876"/>
  <startDate>2014-11-01T00:00:00Z</startDate>
  <expiryDate>2016-11-01T00:00:00Z</expiryDate>
</subscriptionPeriod>
```

``` xml
//Response
HTTP/1.1 201 CREATED
Date: Mon, 06 Oct 2014 10:00:50 GMT
Content-Type: application/vnd.yudu+xml
Location: https://api.yudu.com/Yudu/services/1.0/subscriptionPeriods/7654

<subscriptionPeriod xmlns="http://schema.yudu.com" id="7654">
  <reader id="1234"/>
  <subscription id="9876"/>
  <startDate>2014-11-01T00:00:00Z</startDate>
  <expiryDate>2016-11-01T00:00:00Z</expiryDate>
  <links>
    <link rel="http://schema.yudu.com/subscriptionPeriod" name="self"
          href="https://api.yudu.com/Yudu/services/2.0/subscriptionPeriods/7654"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/reader" name="reader"
          href="https://api.yudu.com/Yudu/services/2.0/readers/1234"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/subscription" name="subscription"
          href="https://api.yudu.com/Yudu/services/2.0/subscriptions/9876"
          type="application/vnd.yudu+xml"/>
  </links>
</subscriptionPeriod>
```

### Resetting Authorised Devices

Suppose a reader has logged in from too many different devices and you would like to reset their authorised devices so that they can log in from a new collection of devices. This is achieved by deleting all authorised devices associated with the reader:

``` xml
//Request
DELETE /Yudu/services/1.0/readers/1234/authorisedComputers/?timestamp=1412586055 HTTP/1.1
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: bGCNgoiseJ7qom8R1czUxoBlmMTSkpRgeccVOQO+VoY=

//Response
HTTP/1.1 204 NO CONTENT
Date: Mon, 06 Oct 2014 10:00:55 GMT
```

### Authenticating A Reader

Given a the username and password for a reader we can check whether such a reader exists and whether the password submitted by the reader is correct. First find a reader.

``` xml
//Request
GET /Yudu/services/1.0/readers/?username=example&timestamp=1412586060 HTTP/1.1
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: h/jKtsHKkNvAb87mElz5xFr9y7/9XaZGcD8qDwjgiFo=

//Response
HTTP/1.1 200 OK
Date: Mon, 11 Jul 2011 10:01:00 GMT
Content-Type: application/vnd.yudu+xml

<readers xmlns="http://schema.yudu.com" limit="100" offset="0" count="1" truncated="false">
  <reader id="5678">
    <username>example</username>
    <emailAddress>user@example.com</emailAddress>
    <firstName>Example</firstName>
    <lastName>User</lastName>
    <authorisedDeviceLimit>3</authorisedDeviceLimit>
    <nodeId>1234</nodeId>
    <links>
      <link rel="http://schema.yudu.com/reader" name="self"
            href="https://api.yudu.com/Yudu/services/2.0/readers/5678"
            type="application/vnd.yudu+xml"/>
      <link rel="http://schema.yudu.com/permissions" name="permissions"
            href="https://api.yudu.com/Yudu/services/2.0/permissions?reader=5678"
            type="application/vnd.yudu+xml"/>
      <link rel="http://schema.yudu.com/readerLogins" name="readerLogins"
            href="https://api.yudu.com/Yudu/services/2.0/readerLogins?reader=5678"
            type="application/vnd.yudu+xml"/>
      <link rel="http://schema.yudu.com/subscriptions" name="subscriptions"
            href="https://api.yudu.com/Yudu/services/2.0/subscriptions?reader=5678"
            type="application/vnd.yudu+xml"/>
      <link rel="http://schema.yudu.com/authorisedDevices" name="authorisedDevices"
            href="https://api.yudu.com/Yudu/services/2.0/readers/5678/authorisedDevices"
            type="application/vnd.yudu+xml"/>
      <link rel="http://schema.yudu.com/authentication" name="authentication"
            href="https://api.yudu.com/Yudu/services/2.0/readers/5678/authentication"
            type="application/vnd.yudu+xml"/>
    </links>
  </reader>
</readers>
```

Then follow the authentication link for the reader and make a **PUT** with an authentication representation:

``` xml
//Request
PUT /Yudu/services/1.0/readers/?username=aUsername&timestamp=1412586065 HTTP/1.1
Accept: application/vnd.yudu+xml
Authentication: abcd1234
Signature: ujp8wI5gGPwIBON3lzQD/ZY5qtR3zLBd/zKc4aNF5/c=

<authentication xmlns="http://schema.yudu.com">
  <password>userPassword</password>
</authentication>

//Response
HTTP/1.1 200 OK
Date: Mon, 11 Jul 2011 10:01:05 GMT
Content-Type: application/vnd.yudu+xml

<authentication xmlns="http://schema.yudu.com">
  <authenticated>false</authenticated>
</authentication>
```

The reader's password is not correct.
