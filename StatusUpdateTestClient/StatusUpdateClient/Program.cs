using System;
using System.Net.Sockets;
using System.IO;

namespace StatusUpdateClient
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Connecting to server...");
            TcpClient client = new TcpClient("localhost", 4242);
            StreamWriter sw = new StreamWriter(client.GetStream());
            StreamReader sr = new StreamReader(client.GetStream());
            Console.WriteLine("Connected\nRequesting Status...");
            sw.WriteLine("status");
            sw.Flush();
            
            Console.WriteLine(sr.ReadLine());
            sw.Close();
            sr.Close();
            client.Close();
            Console.ReadKey();
        }
    }
}
