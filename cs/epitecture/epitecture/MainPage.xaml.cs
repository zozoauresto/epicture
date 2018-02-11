using Imgur.API;
using Imgur.API.Authentication.Impl;
using Imgur.API.Endpoints.Impl;
using Imgur.API.Models;
using Imgur.API.Models.Impl;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Threading.Tasks;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.Storage;
using Windows.Storage.Pickers;
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
        public string CLIENT_ID = "ac00dd791475bae";
        public string CLIENT_SECRET = "33de41b5f2c13e0a8d089e3d52b51925e0210b57";
        string result;
        public MainPage()
        {
            this.InitializeComponent();
        }

        
        public async void tryconnectAsync()
        {
            string startURL = "https://api.imgur.com/oauth2/authorize?client_id=" + CLIENT_ID + "&response_type=token";
            string endURL = "https://imgur.com";
            Uri startURI = new Uri(startURL);
            Uri endURI = new Uri(endURL);
            Debug.WriteLine("ici " + startURI.AbsolutePath);
            

            try
            {
                var webAuthenticationResult =
                    await Windows.Security.Authentication.Web.WebAuthenticationBroker.AuthenticateAsync(
                    Windows.Security.Authentication.Web.WebAuthenticationOptions.None,
                    startURI,
                    endURI);

                switch (webAuthenticationResult.ResponseStatus)
                {
                    case Windows.Security.Authentication.Web.WebAuthenticationStatus.Success:
                        // Successful authentication. 
                        result = webAuthenticationResult.ResponseData.ToString();
                        MyButton.Visibility = Visibility.Collapsed;
                        TextPresentation.Visibility = Visibility.Collapsed;
                        MyImages.Visibility = Visibility.Visible;
                        RectangleMenu.Visibility = Visibility.Visible;
                        ButtonAddImage.Visibility = Visibility.Visible;
                        ButtonMenu.Visibility = Visibility.Visible;
                        break;
                    case Windows.Security.Authentication.Web.WebAuthenticationStatus.ErrorHttp:
                        // HTTP error. 
                        result = webAuthenticationResult.ResponseErrorDetail.ToString();
                        break;
                    default:
                        // Other error.
                        result = webAuthenticationResult.ResponseData.ToString();
                        break;
                }
                Debug.WriteLine("Start : " + result + "|stop");
            }
            catch (Exception ex)
            {
                // Authentication failed. Handle parameter, SSL/TLS, and Network Unavailable errors here. 
                result = ex.Message;
            }
        }

        private void Button_Connection(object sender, RoutedEventArgs e)
        {
            tryconnectAsync();
        }

        private async Task AddImageAsync()
        {
            try
            {
                var client = new ImgurClient(CLIENT_ID, CLIENT_SECRET, new OAuth2Token(getAccess(), getRefresh(), getType(), getId(), getUser(), int.Parse(getExpires())));
                var endpoint = new ImageEndpoint(client);
                IImage image;

                FileOpenPicker openPicker = new FileOpenPicker();
                openPicker.ViewMode = PickerViewMode.Thumbnail;
                openPicker.SuggestedStartLocation = PickerLocationId.PicturesLibrary;
                openPicker.FileTypeFilter.Add(".jpg");
                openPicker.FileTypeFilter.Add(".jpeg");
                openPicker.FileTypeFilter.Add(".png");

                StorageFile file = await openPicker.PickSingleFileAsync();
                if (file != null)
                {
                    // Application now has read/write access to the picked file
                    var files = file.OpenStreamForReadAsync();
                    image = await endpoint.UploadImageStreamAsync(files.Result);
                }
                }
            catch (ImgurException imgurEx)
            {
                Debug.Write("An error occurred uploading an image to Imgur.");
                Debug.Write(imgurEx.Message);
            }
        }

        private void Button_Click_AddImage(object sender, RoutedEventArgs e)
        {
            AddImageAsync();
        }
        private async Task GetImageAsync()
        {
            /*
            var client = new ImgurClient(CLIENT_ID);
            var endpoint = new AccountEndpoint(client);
            var submissions = await endpoint.GetAccountSubmissionsAsync(getUser());
           */
            var client = new ImgurClient(CLIENT_ID);
            var endpoint = new AlbumEndpoint(client);
            var album = await endpoint.GetAlbumAsync("IwjbO");
            Debug.WriteLine("album : " + album + "|stop");
            var images = album.Images;
            ObservableCollection<Windows.UI.Xaml.Controls.Image> tasks = new ObservableCollection<Windows.UI.Xaml.Controls.Image>();
            foreach (var image in album.Images)
            {
                BitmapImage bi3 = new BitmapImage();
                bi3.UriSource = new Uri(image.Link, UriKind.Absolute);
                var im = new Windows.UI.Xaml.Controls.Image();
                im.Stretch = Stretch.Fill;
                im.Source = bi3;
                MyImages.Items.Add(im);
            }
        }
        private void Button_Click_Menu(object sender, RoutedEventArgs e)
        {
            GetImageAsync();
        }

        public string getAccess()
        {
            int pFrom = result.IndexOf("access_token=") + "access_token=".Length;
            int pTo = result.LastIndexOf("&expires");
            return result.Substring(pFrom, pTo - pFrom);
        }
        public string getExpires()
        {
            int pFrom = result.IndexOf("expires_in=") + "expires_in=".Length;
            int pTo = result.LastIndexOf("&token");
            return result.Substring(pFrom, pTo - pFrom);
        }
        public string getType()
        {
            int pFrom = result.IndexOf("token_type=") + "token_type=".Length;
            int pTo = result.LastIndexOf("&refresh");
            return result.Substring(pFrom, pTo - pFrom);
        }
        public string getRefresh()
        {
            int pFrom = result.IndexOf("refresh_token=") + "refresh_token=".Length;
            int pTo = result.LastIndexOf("&account_username");
            return result.Substring(pFrom, pTo - pFrom);
        }
        public string getUser()
        {
            int pFrom = result.IndexOf("account_username=") + "account_username=".Length;
            int pTo = result.LastIndexOf("&account_id");
            return result.Substring(pFrom, pTo - pFrom);
        }
        public string getId()
        {
            int pFrom = result.IndexOf("account_id=") + "account_id=".Length;
            return result.Substring(pFrom, result.Length - pFrom);
        }

        public IImage GetImage(String image_id)
        {
            try
            {
                var client = new ImgurClient(CLIENT_ID, CLIENT_SECRET);
                var endpoint = new ImageEndpoint(client);
                var image = endpoint.GetImageAsync(image_id).GetAwaiter().GetResult();
                Debug.Write("Image retrieved. Image Url: " + image.Link);
                return image;
            }
            catch (ImgurException imgurEx)
            {
                Debug.Write("An error occurred getting an image from Imgur.");
                Debug.Write(imgurEx.Message);
                return null;
            }
        }
    }
}
