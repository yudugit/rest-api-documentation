Changelog
=========

## Major Changes in version 2.0

### URI

The base service URI for version 2.0 API is "https://api.yudu.com/Yudu/services/2.0". The version 1.0 API is still available at the old service URI.

**Note:** v1.0 is deprecated and suppose will be removed when a version 3.0 API is released.

### Links

* `link` elements are now contained within a `links` element in the resource representation xml.
* `link` elements now have a descriptive `name` attribute. In most cases the name is similar to the `rel` attribute, however it is particularly useful for identifying new style self-links (see next item) and [pagination](#pagination) links.
* Self-links (`link` elements with `rel="self"`) now have a the correct `rel` value for the resource type, and a `name` value of `self` for clarity.

For example the v1.0 reader representation

``` xml
<reader xmlns="http://schema.yudu.com" id="5678">
  <username>example</username>
  <emailAddress>user@example.com</emailAddress>
  <firstName>Example</firstName>
  <lastName>User</lastName>
  <authorisedDeviceLimit>3</authorisedDeviceLimit>
  <nodeId>1234</nodeId>
  <link rel="self"
        href="https://api.yudu.com/Yudu/services/1.0/readers/5678"
        type="application/vnd.yudu+xml"/>
  <link rel="http://schema.yudu.com/permissions
        href="https://api.yudu.com/Yudu/services/1.0/permissions?reader=5678"
        type="application/vnd.yudu+xml"/>
  <link rel="http://schema.yudu.com/readerLogins
        href="https://api.yudu.com/Yudu/services/1.0/readerLogins?reader=5678"
        type="application/vnd.yudu+xml"/>
  <link rel="http://schema.yudu.com/subscriptions"
        href="https://api.yudu.com/Yudu/services/1.0/subscriptions?reader=5678"
        type="application/vnd.yudu+xml"/>
  <link rel="http://schema.yudu.com/authorisedDevices"
        href="https://api.yudu.com/Yudu/services/1.0/readers/5678/authorisedDevices"
        type="application/vnd.yudu+xml"/>
  <link rel="http://schema.yudu.com/authentication"
        href="https://api.yudu.com/Yudu/services/1.0/readers/5678/authentication"
        type="application/vnd.yudu+xml"/>
</reader>
```

becomes

``` xml
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

in v2.0.

### Permissions

In version 1.0 a the xml representation in a **PUT** request to a permission resource had to contain both the `reader` and `edition` elements. The request also accepted a `creationDate` element, although it ignored it.
In version 2.0 a valid permission representation for a **PUT** request may contain only the `expiryDate` element. Any other element will cause a validation failure. This is to bring this method inline with other **PUT** requests in the API.

For full details see the [permissions section of the readme](README.md#permission).

### List Resources

* List resources contain an extra level of nesting to allow for [pagination](#pagination). The root element name remains the same, but rather than a list of resources this element now contains a `{resourceName}List` element containing the resource representations, and a `links` element containing any relevant pagination links.
* List resources have additional attributes on the root attribute for pagination purposes.
* The `xmlns` declaration has been moved from each individual resource representation to the root element.

For example the v1.0 subscription list

``` xml
<subscriptions>
  <subscription xmlns="http://schema.yudu.com" id="9876">
    <title>Universal Subscription</title>
    <onDeviceTitle>subscription on device</onDeviceTitle>
    <subscriptionType>universal_club</subscriptionType>
    <disabled>false</disabled>
    <defaultAuthorisedDeviceLimit>6</defaultAuthorisedDeviceLimit>
    <nodeId>1234</nodeId>
    <link rel="self"
          href="https://api.yudu.com/Yudu/services/1.0/subscriptions/9876"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/readers"
          href="https://api.yudu.com/Yudu/services/1.0/readers?subscription=9876"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/editions"
          href="https://api.yudu.com/Yudu/services/1.0/editions?subscription=9876"
          type="application/vnd.yudu+xml"/>
  </subscription>
  <subscription xmlns="http://schema.yudu.com" id="5432">
    <title>Example Subscription</title>
    <onDeviceTitle>A Subscription</onDeviceTitle>
    <subscriptionType>flash_node</subscriptionType>
    <disabled>true</disabled>
    <defaultAuthorisedDeviceLimit>3</defaultAuthorisedDeviceLimit>
    <nodeId>1234</nodeId>
    <link rel="self"
          href="https://api.yudu.com/Yudu/services/1.0/subscriptions/5432"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/readers"
          href="https://api.yudu.com/Yudu/services/1.0/readers?subscription=5432"
          type="application/vnd.yudu+xml"/>
    <link rel="http://schema.yudu.com/editions"
          href="https://api.yudu.com/Yudu/services/1.0/editions?subscription=5432"
          type="application/vnd.yudu+xml"/>
  </subscription>
</subscriptions>
```

becomes

``` xml
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
in v2.0.

### Pagination

Version 2.0 adds support for pagination and sorting for **GET** requests to list resources. The root element of list resource now contains 4 pagination attributes: `limit`, `offset`, `count` and `truncated` as well as a new `previous` and `next` links for navigation.

Similarly 3 new query parameters are now supported for all **GET** requests to list resources: `limit`, `offset` and `sort`.

For full details see the [pagination section of the readme](README.md#pagination).
