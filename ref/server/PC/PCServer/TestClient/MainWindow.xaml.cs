using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using QingNang.ProtocolClass;
using LitJson;
using System.Xml;
using QingNang.NetWorkLayer;

namespace QingNang.TestClient
{
    /// <summary>
    /// Window1.xaml 的交互逻辑
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
            string tp = System.Diagnostics.Process.GetCurrentProcess().MainModule.FileName;
            strPath = tp.Substring(0, tp.LastIndexOf("\\"));
            iListenPort = 8866;
            iMaxListenThreadNumber = 30;

            ParserConfig();

            snwNetWork = new SocketNetWork();
            snwNetWork.InitNetWorkComponent(iListenPort, iMaxListenThreadNumber);
        }

        private void ParserConfig()
        {
            XmlDocument xdoc = new XmlDocument();
            try
            {
                xdoc.Load(strPath + "\\Config\\software_config.xml");
                XmlNode xn = xdoc.SelectSingleNode("Config");
                if (xn != null)
                {
                    int iTmp = 0;
                    foreach (XmlNode xnd in xn.ChildNodes)
                    {
                        try
                        {
                            switch (xnd.Name)
                            {
                                case "NetWork":
                                    if (int.TryParse(xnd.Attributes["ListenPort"].InnerText, out iTmp) == true)
                                    {
                                        iListenPort = iTmp;
                                    }
                                    else
                                    {
                                        iListenPort = 8866;
                                    }
                                    if (int.TryParse(xnd.Attributes["MaxListenThreadNumber"].InnerText, out iTmp) == true)
                                    {
                                        iMaxListenThreadNumber = iTmp;
                                    }
                                    else
                                    {
                                        iMaxListenThreadNumber = 30;
                                    }
                                    break;
                            }
                        }
                        catch (Exception e)
                        {
                        }
                    }
                }
            }
            catch (Exception e)
            {
            }
        }


        private void btOk_Click(object sender, RoutedEventArgs e)
        {
            int iOp = 0;
            if (int.TryParse(tbOpCode.Text, out iOp) == false || iOp < 1 || iOp > 8)
            {
                MessageBox.Show("操作码为1-8之间的数字！");
                return;
            }

            string strUid = "102012030203/test";
            Object obj = null;
            switch (iOp)
            {
                case 1:
                    RequestMedicineXml rmx = new RequestMedicineXml();
                    rmx.op = iOp;
                    rmx.uid = strUid;
                    rmx.v = 1;
                    rmx.dt = new MedicineRequestItem[2];
                    MedicineRequestItem mri = new MedicineRequestItem();
                    mri.ow = true;
                    mri.pd = "6901694108536";
                    rmx.dt[0] = mri;
                    mri = new MedicineRequestItem();
                    mri.ow = false;
                    mri.pd = "6921151700295";
                    rmx.dt[1] = mri;
                    obj = rmx;
                    break;
                case 2:
                    RequestAddRemind rar = new RequestAddRemind();
                    rar.op = 2;
                    rar.uid = strUid;
                    rar.v = 1;
                    rar.dt = new RemindInfo[2];
                    RemindInfo mi = new RemindInfo();
                    mi.tp = 0;
                    mi.pd = "6901694108536";
                    mi.nb = 1.2;
                    mi.st = "2012-3-31";
                    mi.et = "2012-4-2";
                    rar.dt[0] = mi;
                    mi = new RemindInfo();
                    mi.tp = 5;
                    mi.nb = 4;
                    mi.pd = "6921151700295";
                    mi.st = "2012-4-2 13:13:13/2012-4-4 13:13:13/2012-4-6 13:13:13";
                    mi.et = null;
                    rar.dt[1] = mi;
                    obj = rar;
                    break;
                case 3:
                    RequestPicture rpp = new RequestPicture();
                    rpp.op = 3;
                    rpp.v = 1;
                    rpp.dt = "6901694108536";
                    obj = rpp;
                    break;
                case 4:
                    RequestSuggest rs = new RequestSuggest();
                    rs.op = 4;
                    rs.uid = strUid;
                    rs.v = 1;
                    rs.dt = new string[] { "6901694108536", "6921151700295" };
                    obj = rs;
                    break;
                case 5:
                    RequestVer rv = new RequestVer();
                    rv.op = 5;
                    rv.uid = strUid;
                    rv.v = 1;
                    rv.vr = "1.0.0.1";
                    obj = rv;
                    break;
                case 6:
                    RequestUploadXml rux = new RequestUploadXml();
                    rux.op = 6;
                    rux.v = 1;
                    rux.dt = new string[] { "6901694108536", "6921151700295" };
                    obj = rux;
                    break;
                case 7:
                    RequestUploadXml rux1 = new RequestUploadXml();
                    rux1.op = 7;
                    rux1.v = 1;
                    rux1.dt = new string[] { "6901694108536", "6921151700295" };
                    obj = rux1;
                    break;
                case 8:
                    RequestEditName ren = new RequestEditName();
                    ren.op = 8;
                    ren.uid = strUid;
                    ren.uido = strUid;
                    ren.v = 1;
                    obj = ren;
                    break;
            }

            String strSend = JsonMapper.ToJson(obj);
            snwNetWork.SendDatas(strSend);
        }

        protected string strPath { get; set; }
        protected int iListenPort { get; set; }
        protected int iMaxListenThreadNumber { get; set; }
        protected SocketNetWork snwNetWork { get; set; }
    }
}
