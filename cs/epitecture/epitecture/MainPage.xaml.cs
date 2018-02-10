using Imgur.API;
using Imgur.API.Endpoints.Impl;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Threading.Tasks;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;
using Windows.UI.Xaml.Navigation;

// Pour plus d'informations sur le modèle d'élément Page vierge, consultez la page https://go.microsoft.com/fwlink/?LinkId=402352&clcid=0x409

namespace epitecture
{
    /// <summary>
    /// Une page vide peut être utilisée seule ou constituer une page de destination au sein d'un frame.
    /// </summary>
    public sealed partial class MainPage : Page
    {
        public MainPage()
        {
            this.InitializeComponent();
          //  GetImage();
        }
        private async void tokenButton_Click(object sender, RoutedEventArgs e)
        {
            await Windows.System.Launcher.LaunchUriAsync(new Uri("https://api.imgur.com/oauth2/authorize?client_id=ac00dd791475bae&response_type=pin"));
        }

        private async void buttonConnection_Click(object sender, RoutedEventArgs e)
        {
            String pin = pinBox.Text;
            if (pin.Length > 0)
            {
                using (HttpClient client = new HttpClient())
                {
                    var values = new Dictionary<string, string>
                    {
                        { "client_id", "ac00dd791475bae" },
                        { "client_secret", "33de41b5f2c13e0a8d089e3d52b51925e0210b57" },
                        { "grant_type", "pin" },
                        { "pin", pinBox.Text }
                    };
                    var content = new FormUrlEncodedContent(values);
                    var response = await client.PostAsync("https://api.imgur.com/oauth2/token", content);
                    string ret = await response.Content.ReadAsStringAsync();
                    Debug.WriteLine("ret : " + ret);
                    Debug.WriteLine("|stop");

                    var getResponse = await client.GetAsync("https://api.imgur.com/3/gallery/");
                    Debug.WriteLine("here we have : " + getResponse);
                    Debug.WriteLine("|the end");

                    /*
                    ImgurUWP_Imgur Imgur = JsonConvert.DeserializeObject<ImgurUWP_Imgur>(ret);
                    if (Imgur.account_id != 0)
                    {
                        this.Frame.Navigate(typeof(SecondPage), Imgur);
                    }
                    else
                    {
                        Imgur_Request request = JsonConvert.DeserializeObject<Imgur_Request>(ret);
                        infoBlock.Text = "Error : " + request.data.error;
                    }
                    */

                }
            }
            else
            {
                infoBlock.Text = "Error : Empty pin";
            }
        }

        private void pinBox_PointerEntered(object sender, Windows.UI.Xaml.Input.PointerRoutedEventArgs e)
        {
            if (pinBox.Text.CompareTo("PIN code") == 0)
            {
                pinBox.Text = "";
            }
        }

        private void pinBox_TextChanged(object sender, TextChangedEventArgs e)
        {

        }
        /*   public async Task GetImage()
           {
               try
               {
                   var client = new Imgur.API.Authentication.Impl.ImgurClient("ac00dd791475bae", "33de41b5f2c13e0a8d089e3d52b51925e0210b57");
                   var endpoint = new ImageEndpoint(client);
                   var image = await endpoint.GetImageAsync("M29eb");
                   Debug.Write("Image retrieved. Image Url: " + image.Link);
                   BitmapImage bi3 = new BitmapImage();
                   bi3.UriSource = new Uri(image.Link, UriKind.Absolute);
                   trythis.Stretch = Stretch.Fill;
                   trythis.Source = bi3;

               }
               catch (ImgurException imgurEx)
               {
                   Debug.Write("An error occurred getting an image from Imgur.");
                   Debug.Write(imgurEx.Message);
               }
           }
           */
    }
}
