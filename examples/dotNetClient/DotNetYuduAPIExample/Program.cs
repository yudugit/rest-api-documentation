using System;
using System.Globalization;
using System.Security.Cryptography;
using System.Text;
using RestSharp;

namespace DotNetYuduAPIExample
{
    internal class Program
    {
        //Please contact Yudu if you don't already have these keys.
        private const string SecretKey = "(Shared secret goes here)";
        private const string ApiKey = "(API key goes here)";
        private const string Domain = "https://api.yudu.com";
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

            var postPath = ReaderPath(UnixTimestamp);
            var postUri = Domain + postPath;

            var stringToSign = string.Format("POST{0}{1}", postPath, PostXml);
            var signature = Signature(stringToSign);
            Console.WriteLine(signature);
            var postBody = Encoding.UTF8.GetBytes(PostXml);

            var request = new RestRequest(Method.POST)
                .AddHeader("Authentication", ApiKey)
                .AddHeader("Signature", signature)
                .AddParameter("application/vnd.yudu+xml", postBody, ParameterType.RequestBody);

            var client = new RestClient(postUri);
            var response = client.Execute(request);

            Console.WriteLine(response.StatusCode);
            Console.WriteLine(response.Content);

        }

        private static string ReaderPath (string timeStamp)
        {
            return string.Format("/Yudu/services/2.0/readers?timestamp={0}", timeStamp);
        }

        private static string UnixTimestamp
        {
            get
            {
                return ((int) (DateTime.UtcNow - new DateTime(1970, 1, 1)).TotalSeconds).ToString(CultureInfo.InvariantCulture);
            }
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