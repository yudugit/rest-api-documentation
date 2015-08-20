using System;
using System.Globalization;
using System.Security.Cryptography;
using System.Text;
using RestSharp;

namespace DotNetYuduAPIExample
{
    internal class Program
    {
        // Please contact Yudu if you don't already have these keys.
        private const string SecretKey = "(Shared secret goes here)";
        private const string ApiKey = "(API key goes here)";
        private const string Domain = "https://api.yudu.com";
        private const string ServicePathRoot = "/Yudu/services/2.0";
        private const string PostXml =
@"<reader xmlns=""http://schema.yudu.com"">
	<username>example</username>
	<emailAddress>user@example.com</emailAddress>
	<firstName>Example</firstName>
	<lastName>User</lastName>
	<password>password</password>
	<nodeId>12345678</nodeId>
</reader>";


        private static void Main()
        {
            var fullPath = GetReaderPath();
            var fullUri = Domain + fullPath;

            // Change this to false to try a POST request instead of a GET request
            var doGetNotPost = true;

            if (doGetNotPost)
            {
                var request = TryGetRequest(fullPath);
            }
            else // do POST not GET
            {
                var request = TryPostRequest(fullPath);
            }

            var client = new RestClient(fullUri);
            var response = client.Execute(request);

            // Print out status code and response body
            Console.WriteLine(response.StatusCode);
            Console.WriteLine(response.Content);
        }

        private static IRestRequest TryGetRequest(string getPath)
        {
            var stringToSign = string.Format("GET{0}", getPath);
            var signature = Signature(stringToSign);
            var request = new RestRequest(Method.GET)
                    .AddHeader("Authentication", ApiKey)
                    .AddHeader("Signature", signature)
                    .AddHeader("Accept", "application/vnd.yudu+xml");
            return request;
        }

        private static IRestRequest TryPostRequest(string postPath)
        {
            var stringToSign = string.Format("POST{0}{1}", postPath, PostXml);
            var signature = Signature(stringToSign);
            var postBody = Encoding.UTF8.GetBytes(PostXml);
            var request = new RestRequest(Method.POST)
                .AddHeader("Authentication", ApiKey)
                .AddHeader("Signature", signature)
                .AddParameter("application/vnd.yudu+xml", postBody, ParameterType.RequestBody);
            return request;
        }

        private static string GetReaderPath()
        {
            return string.Format("{0}/readers?timestamp={1}", ServicePathRoot, GetCurrentUnixTimestamp());
        }

        private static string GetCurrentUnixTimestamp()
        {
            var epoch = new DateTime(1970, 1, 1);
            var secondsSinceEpoch = (long)(DateTime.UtcNow - epoch).TotalSeconds;
            return secondsSinceEpoch.ToString(CultureInfo.InvariantCulture);
        }

        private static string Signature(string stringToSign)
        {
            var postBytes = Encoding.UTF8.GetBytes(stringToSign);
            var secretBytes = Encoding.UTF8.GetBytes(SecretKey);
            var hash = new HMACSHA256(secretBytes);
            var signatureBytes = hash.ComputeHash(postBytes);
            return Convert.ToBase64String(signatureBytes);
        }
    }
}