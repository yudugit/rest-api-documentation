Yudu Publisher REST API v2.0
============================

## Introduction

This Document outlines the web service interface provided by YUDU to enable management of readers, purchases and subscriptions for digital editions.

Access to the service requires a Yudu Publisher account with the "REST API" permission and a valid API token created through thr Yudu Publisher interface.

### Overview

The Yudu API uses the "Representational state transfer" (REST) architectural style. In particular, it applies the "hypermedia as the engine of application state" (HATEOAS) principle in the design of the resources. If you are not already familiar with these principles then we recommend reading [REST in Practice](http://restinpractice.com/book/) as an introduction before diving further into the Yudu API.

### Terminology

The following terminology is used in this document:

- **Reader** - An end user that will be reading your digital editions
- **Edition** - A Yudu digital edition
- **Permission** - Refers to the granting of access to one edition for one reader
- **Reader login** - A particular instance of a reader accessing an edition
- **Publication** - A Yudu publication (also known as a "group")
- **Subscription** - A Yudu subscription
- **Subscription period** - Refers to the granting of access to a subscription for a time period
- **Authorised device** - A single device which has been used by a reader to access an edition.
- **Node** - The Yudu Publisher system is arranged into a hierarchy of nodes. For most users you won't need to worry about the node ID of your Readers, however if you would like to place them at different levels within your part of the hierarchy you can by specifying it.

### Overview

This service is arranged into **resources**, such as readers and their permissions. Each resource can be operated on by using different **HTTP methods** or **verbs** such as such as **GET**, **POST**, **PUT**, **DELETE** and **OPTIONS**.

### Sample code

#### PHP Client

We have produced a sample PHP application that shows the use of the API. Note that this code is not ready for production use, and serves as an example only. It is not extensively tested and does not handle every possible error case in a suitable manner for production use. In addition, it does not represent best practices for implementing a client of the API. For example, as noted below, rather than using the URIs as described, you are encouraged to make use of the hypermedia present in the resources to navigate the API, decoupling your implementation from ours.

#### Java Client

We have produced a simple GUI java application that can be used to build and send requests to our service. Note that as for the PHP client, this code is not reader for production use and serves as an example only. 

#### Ruby Client

We have produced a simple ruby command line tool to calculate the correct Base64 encoded HMAC SHA256 hash for any string and shared secret (see [Request Authentication](#request-authentication)). This can be used to check that your signing method is creating the correct signature.

All code samples can be found the [examples](examples) directory and each is accompanied by a README.md file which contains the documentation.

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

*Relations* or *Link Relations* provide a way to navigate this service. Each XML representation of a resource will contain a number if *links* which reference other URIs. These URIs indicate the logical next steps when using this system.

Resource representations returned by this service may contains a `links` element which contains a list of `link` elements detailing a link to another resource or list of resources. They are represented in XML as follows:

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
 
In order to determine which verbs can be used to interact with a resource, it is reccomended to make an **OPTIONS** request to the URI given in the link relation. This will return an `Allow` header containing a comma-separated list of allowed verbs.

### URI Summary

The following table summarises all the available resource URIs, and the effect of each verb on them. Each of them is relative to the base URI for our API: `https://api.yudu.com/Yudu/services/2.0`.

| Resource                        | GET                                                 | POST                                  | PUT                               | DELETE                                      |
| ------------------------------- | --------------------------------------------------- | ------------------------------------- | --------------------------------- | ------------------------------------------- |
| /                               | Returns a list of links to the other available URIs | N/A                                   | N/A                               | N/A                                         |
| /readers/                       | Returns a list of readers                           | Creates a new reader                  | N/A                               | N/A                                         |
| /readers/<id>                   | Returns the details of a single reader              | N/A                                   | Updates a reader                  | Deletes a reader                            |
| /editions/                      | Gets a list of all editions                         | N/A                                   | N/A                               | N/A                                         |
| /editions/<id>                  | Gets the details of a single edition                | N/A                                   | N/A                               | N/A                                         |
| /permissions/                   | Lists all edition permissions by readers            | Creates a new permission for a reader | N/A                               | N/A                                         |
| /permissions/<id>               | Gets the details of a single permission             | N/A                                   | Updates a permission              | Removes an existing permission              |
| /readerLogins/                  | Gets a list of all reader logins                    | N/A                                   | N/A                               | N/A                                         |
| /readerLogins/<id>              | Gets the details of a single reader login           | N/A                                   | N/A                               | N/A                                         |
| /publications/                  | Gets a list of all publications                     | N/A                                   | N/A                               | N/A                                         |
| /publications/<id>              | Gets the details of a single publication            | N/A                                   | N/A                               | N/A                                         |
| /subscriptions/                 | Gets a list of subscriptions                        | N/A                                   | N/A                               | N/A                                         |
| /subscriptions/<id>             | Gets the details of a single subscription           | N/A                                   | N/A                               | N/A                                         |
| /subscriptionPeriods/           | Gets a list of subscription periods                 | N/A                                   | N/A                               | N/A                                         |
| /subscriptionPeriods/<id>       | Gets the details of a single subscription period    | Creates a new subscription period     | Updates a subscription period     | Removes an existing subscription period     |
| /readers/<id>/authorisedDevices | N/A                                                 | N/A                                   | N/A                               | Removes all authorised devices for a reader |
| /readers/<id>/authentication    | N/A                                                 | N/A                                   | Authenticates a reader's password | N/A                                         |

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

While not technically a resource this endpoint is the starting point for any interaction with the API. It is assumed thata user of the API does not know how to construct the URIs for any resource or the IDs or URIs of any existing resource. Instead a user starts at the service description and follows links to navigate the resource.

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
    ⋮ // some link elemenets
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
| **subscription** | Integer | Return only readers subscribed to the subscription with the given ID |

##### POST

A **POST** request creates a new reader. The request body must contain the XML representation of a reader with the required fields as detailed in [Permissible Fields](#reader-permissible-fields).

A successful **POST** will result in a **201 CREATED** response with a `Location` header specifying the URI of the newly created resource and the repsonse body will contain the XML representation of the resource (including the `id` and `links`).

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

#### Sortable Fields

Editions can be sorted by the following fields (see [Pagination](#pagination) for details):

- `id`
- `name`
- `publishedDate`

#### Edition List

| URI          | Relation                          | Verbs   |
| ------------ | --------------------------------- | ------- |
| `/editions/` | `http://schema.yudu.com/editions` | **GET** |

##### GET

A **GET** request returns the XML representation of a list of editions, optionally filtered using the following query string parameters, as well as the pagination parameters described in [Pagination](#pagination).

| Filter | Type | Description |
| ------ | ---- | ----------- |
| **name** | String | Filter by edition name *prefix* |
| **subscription** | Integer | Return only editions shipped to the subscription with the given ID |
| **publishedDate\_after** | [Date](#date) |  Return only editions with an official publication date *after* the given date |
| **publishedDate\_before** | [Date](#date) | Return only editions with an official publication date *before* the given date |
| **flashPublished** | [Boolean](boolean) | Return only editions which are published (or not published) on the flash platform |
| **iOSPublished** | [Boolean](boolean) | Return only editions which are published (or not published) on the iOS platform |
| **androidPublished** | [Boolean](boolean) | Return only editions which are published (or not published) on the android platform
| **htmlPublished** | [Boolean](boolean) | Return only editions which are published (or not published) on the HTML5 platform |
| **webPublished** | [Boolean](boolean) | Return only editions which are published (or not published) on the combined web platform |

#### Single Edition

| URI              | Relation                         | Verbs   |
| ---------------- | -------------------------------- | ------- |
| `/editions/{id}` | `http://schema.yudu.com/edition` | **GET** |

##### GET

A **GET** request returns the XML representation of the edition. Note that any fields which do not have a value may not be included in the XML representation.

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
| **creationDate\_after** | [Date](#date) |  Return only permissions with a creation date *after* the given date |
| **creationDate\_before** | [Date](#date) | Return only permissions with a creation date *before* the given date |
| **expiry\_after** | [Date](#date) |  Return only permissions with an expiry date *after* the given date |
| **expiry\_before** | [Date](#date) | Return only permissions with an expiry date *before* the given date |

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


### Subscripion

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
| **disabled** | [Boolean](boolean) | Return only subscriptions which are disable (or not disabled) |
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
| **startDate\_after** | [Date](#date) |  Return only subscription periods with a start date date *after* the given date |
| **startDate\_before** | [Date](#date) | Return only subscription periods with a start date date *before* the given date |
| **expiry\_after** | [Date](#date) |  Return only subscription periods with an expiry date *after* the given date |
| **expiry\_before** | [Date](#date) | Return only subscription periods with an expiry date *before* the given date |

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
| **loginDate\_after** | [Date](#date) |  Return only reader logins *after* the given date |
| **loginDate\_before** | [Date](#date) | Return only reader logins *before* the given date |
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
| **iDeviceEnabled** | [Boolean](boolean) | Return only publications which are enabled (or disabled) on the *idevice* platform |
| **androidEnabled** | [Boolean](boolean) | Return only publications which are enabled (or disabled) on the *android*  / *air* platform |

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

## Technical Details

### Request Authentication

#### Overview

Each request must be accompanied by a two piece authentication scheme. To access the service a *key* and *shared secret* must be used. These can be obtained and managed through the Yudu Publisher interface. The *key* is used for identification and the *shared secret* is used to sign each request. Both the *key* and *signature* should be included in specific request headers.

#### Authentication Header

The API *key* must be supplied with each request as a request header called `Authentication`.

#### Timestamp Query Parameter

The query parameters in the URI of each request must include `timestamp` - a [unix epoch timestamp](http://en.wikipedia.org/wiki/Unix_time) (in seconds) of the request. The timestamp is checked on our server to protect against replay attacks. You must make sure your server time is set accurately otherwise your requests may be rejected.

#### Signature Header

The shared secret should be used as the signing token used to generate a base-64 encoded HMAC-SH256 hash of a specific string.

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

`sort=name_asc,publishedDate`

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
