﻿#pragma checksum "C:\Users\youss\Documents\epicture\cs\epitecture\epitecture\MainPage.xaml" "{406ea660-64cf-4c82-b6f0-42d48172a799}" "FD6E4D956037113BE6C189CCA63BCC6F"
//------------------------------------------------------------------------------
// <auto-generated>
//     This code was generated by a tool.
//
//     Changes to this file may cause incorrect behavior and will be lost if
//     the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace epitecture
{
    partial class MainPage : 
        global::Windows.UI.Xaml.Controls.Page, 
        global::Windows.UI.Xaml.Markup.IComponentConnector,
        global::Windows.UI.Xaml.Markup.IComponentConnector2
    {
        /// <summary>
        /// Connect()
        /// </summary>
        [global::System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Windows.UI.Xaml.Build.Tasks"," 14.0.0.0")]
        [global::System.Diagnostics.DebuggerNonUserCodeAttribute()]
        public void Connect(int connectionId, object target)
        {
            switch(connectionId)
            {
            case 1:
                {
                    this.MyButton = (global::Windows.UI.Xaml.Controls.Button)(target);
                    #line 10 "..\..\..\MainPage.xaml"
                    ((global::Windows.UI.Xaml.Controls.Button)this.MyButton).Click += this.Button_Connection;
                    #line default
                }
                break;
            case 2:
                {
                    this.MyImages = (global::Windows.UI.Xaml.Controls.ListView)(target);
                    #line 11 "..\..\..\MainPage.xaml"
                    ((global::Windows.UI.Xaml.Controls.ListView)this.MyImages).SelectionChanged += this.OnClickItem;
                    #line default
                }
                break;
            case 3:
                {
                    this.RectangleMenu = (global::Windows.UI.Xaml.Shapes.Rectangle)(target);
                }
                break;
            case 4:
                {
                    this.ButtonAddImage = (global::Windows.UI.Xaml.Controls.Button)(target);
                    #line 27 "..\..\..\MainPage.xaml"
                    ((global::Windows.UI.Xaml.Controls.Button)this.ButtonAddImage).Click += this.Button_Click_AddImage;
                    #line default
                }
                break;
            case 5:
                {
                    this.ButtonMenu = (global::Windows.UI.Xaml.Controls.Button)(target);
                    #line 28 "..\..\..\MainPage.xaml"
                    ((global::Windows.UI.Xaml.Controls.Button)this.ButtonMenu).Click += this.Button_Click_Menu;
                    #line default
                }
                break;
            case 6:
                {
                    this.TextPresentation = (global::Windows.UI.Xaml.Controls.TextBlock)(target);
                }
                break;
            case 7:
                {
                    this.ButtonGetGaleryTag = (global::Windows.UI.Xaml.Controls.Button)(target);
                    #line 30 "..\..\..\MainPage.xaml"
                    ((global::Windows.UI.Xaml.Controls.Button)this.ButtonGetGaleryTag).Click += this.Button_Click_Galery_Tag;
                    #line default
                }
                break;
            case 8:
                {
                    this.TextBoxTag = (global::Windows.UI.Xaml.Controls.TextBox)(target);
                }
                break;
            case 9:
                {
                    this.ButtonGetGalerySearch = (global::Windows.UI.Xaml.Controls.Button)(target);
                    #line 32 "..\..\..\MainPage.xaml"
                    ((global::Windows.UI.Xaml.Controls.Button)this.ButtonGetGalerySearch).Click += this.Button_Click_Galery_Search;
                    #line default
                }
                break;
            case 10:
                {
                    this.TextBoxSearch = (global::Windows.UI.Xaml.Controls.TextBox)(target);
                }
                break;
            default:
                break;
            }
            this._contentLoaded = true;
        }

        [global::System.CodeDom.Compiler.GeneratedCodeAttribute("Microsoft.Windows.UI.Xaml.Build.Tasks"," 14.0.0.0")]
        [global::System.Diagnostics.DebuggerNonUserCodeAttribute()]
        public global::Windows.UI.Xaml.Markup.IComponentConnector GetBindingConnector(int connectionId, object target)
        {
            global::Windows.UI.Xaml.Markup.IComponentConnector returnValue = null;
            return returnValue;
        }
    }
}

