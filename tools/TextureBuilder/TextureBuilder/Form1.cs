using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace TextureBuilder
{
    public partial class Form1 : Form
    {
        Bitmap baseImage;
        Bitmap trimImage;
        Bitmap compiledImage;

        Bitmap drawerSide;
        Bitmap drawerSideH;
        Bitmap drawerSideV;
        Bitmap drawerFront1;
        Bitmap drawerFront2;
        Bitmap drawerFront4;
        Bitmap drawerSort;
        Bitmap drawerTrim;

        SolidBrush shadeBrush = new SolidBrush(Color.FromArgb(32, 0, 0, 0));
        SolidBrush handleBrush = new SolidBrush(Color.FromArgb(192, 0, 0, 0));

        public Form1 ()
        {
            InitializeComponent();
        }

        private void compileSide ()
        {
            drawerSide = new Bitmap(baseImage.Size.Width, baseImage.Size.Height, PixelFormat.Format32bppArgb);
            using (Graphics g = Graphics.FromImage(drawerSide)) {
                g.InterpolationMode = InterpolationMode.NearestNeighbor;
                g.PixelOffsetMode = PixelOffsetMode.Half;
                g.DrawImage(baseImage, 0, 0, drawerSide.Size.Width, drawerSide.Size.Height);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 0, 16, 1), scaledRect(trimImage, 0, 0, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 15, 16, 1), scaledRect(trimImage, 0, 15, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 1, 1, 14), scaledRect(trimImage, 0, 1, 1, 14), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 15, 1, 1, 14), scaledRect(trimImage, 15, 1, 1, 14), GraphicsUnit.Pixel);
            }
        }

        private void compileSideH ()
        {
            drawerSideH = new Bitmap(baseImage.Size.Width, baseImage.Size.Height, PixelFormat.Format32bppArgb);
            using (Graphics g = Graphics.FromImage(drawerSideH)) {
                g.InterpolationMode = InterpolationMode.NearestNeighbor;
                g.PixelOffsetMode = PixelOffsetMode.Half;
                g.DrawImage(baseImage, 0, 0, drawerSide.Size.Width, drawerSide.Size.Height);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 0, 16, 1), scaledRect(trimImage, 0, 0, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 15, 16, 1), scaledRect(trimImage, 0, 15, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 1, 1, 14), scaledRect(trimImage, 0, 1, 1, 14), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 15, 1, 1, 14), scaledRect(trimImage, 15, 1, 1, 14), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 1, 7, 14, 2), scaledRect(trimImage, 1, 7, 14, 2), GraphicsUnit.Pixel);
            }
        }

        private void compileSideV ()
        {
            drawerSideV = new Bitmap(baseImage.Size.Width, baseImage.Size.Height, PixelFormat.Format32bppArgb);
            using (Graphics g = Graphics.FromImage(drawerSideV)) {
                g.InterpolationMode = InterpolationMode.NearestNeighbor;
                g.PixelOffsetMode = PixelOffsetMode.Half;
                g.DrawImage(baseImage, 0, 0, drawerSide.Size.Width, drawerSide.Size.Height);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 0, 16, 1), scaledRect(trimImage, 0, 0, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 15, 16, 1), scaledRect(trimImage, 0, 15, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 1, 1, 14), scaledRect(trimImage, 0, 1, 1, 14), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 15, 1, 1, 14), scaledRect(trimImage, 15, 1, 1, 14), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 7, 1, 2, 14), scaledRect(trimImage, 7, 1, 2, 14), GraphicsUnit.Pixel);
            }
        }

        private void compileSort ()
        {
            drawerSort = new Bitmap(baseImage.Size.Width, baseImage.Size.Height, PixelFormat.Format32bppArgb);
            using (Graphics g = Graphics.FromImage(drawerSort)) {
                g.InterpolationMode = InterpolationMode.NearestNeighbor;
                g.PixelOffsetMode = PixelOffsetMode.Half;
                g.DrawImage(baseImage, 0, 0, drawerSide.Size.Width, drawerSide.Size.Height);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 0, 16, 1), scaledRect(trimImage, 0, 0, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 15, 16, 1), scaledRect(trimImage, 0, 15, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 1, 1, 14), scaledRect(trimImage, 0, 1, 1, 14), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 15, 1, 1, 14), scaledRect(trimImage, 15, 1, 1, 14), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 4, 4, 8, 2), scaledRect(trimImage, 4, 4, 8, 2), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 5, 6, 6, 1), scaledRect(trimImage, 5, 6, 6, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 6, 7, 4, 1), scaledRect(trimImage, 6, 7, 4, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 7, 8, 2, 4), scaledRect(trimImage, 7, 8, 2, 4), GraphicsUnit.Pixel);
            }
        }

        private void compileFront1 ()
        {
            drawerFront1 = new Bitmap(baseImage.Size.Width, baseImage.Size.Height, PixelFormat.Format32bppArgb);
            using (Graphics g = Graphics.FromImage(drawerFront1)) {
                g.InterpolationMode = InterpolationMode.NearestNeighbor;
                g.PixelOffsetMode = PixelOffsetMode.Half;
                g.DrawImage(baseImage, 0, 0, drawerSide.Size.Width, drawerSide.Size.Height);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 0, 16, 1), scaledRect(trimImage, 0, 0, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 15, 16, 1), scaledRect(trimImage, 0, 15, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 1, 1, 14), scaledRect(trimImage, 0, 1, 1, 14), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 15, 1, 1, 14), scaledRect(trimImage, 15, 1, 1, 14), GraphicsUnit.Pixel);
                g.FillRectangle(handleBrush, scaledRect(drawerFront1, 6, 1, 4, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 1, 14, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 2, 1, 12));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 14, 2, 1, 12));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 15, 15, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 15, 1, 1, 14));
            }
        }

        private void compileFront2 ()
        {
            drawerFront2 = new Bitmap(baseImage.Size.Width, baseImage.Size.Height, PixelFormat.Format32bppArgb);
            using (Graphics g = Graphics.FromImage(drawerFront2)) {
                g.InterpolationMode = InterpolationMode.NearestNeighbor;
                g.PixelOffsetMode = PixelOffsetMode.Half;
                g.DrawImage(baseImage, 0, 0, drawerSide.Size.Width, drawerSide.Size.Height);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 0, 16, 1), scaledRect(trimImage, 0, 0, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 15, 16, 1), scaledRect(trimImage, 0, 15, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 1, 1, 14), scaledRect(trimImage, 0, 1, 1, 14), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 15, 1, 1, 14), scaledRect(trimImage, 15, 1, 1, 14), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 1, 7, 14, 2), scaledRect(trimImage, 1, 7, 14, 2), GraphicsUnit.Pixel);

                g.FillRectangle(handleBrush, scaledRect(drawerFront1, 6, 1, 4, 1));
                g.FillRectangle(handleBrush, scaledRect(drawerFront1, 6, 9, 4, 1));

                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 1, 14, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 9, 14, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 2, 1, 4));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 14, 2, 1, 4));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 10, 1, 4));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 14, 10, 1, 4));

                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 7, 15, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 15, 15, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 15, 1, 1, 6));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 15, 9, 1, 6));
            }
        }

        private void compileFront4 ()
        {
            drawerFront4 = new Bitmap(baseImage.Size.Width, baseImage.Size.Height, PixelFormat.Format32bppArgb);
            using (Graphics g = Graphics.FromImage(drawerFront4)) {
                g.InterpolationMode = InterpolationMode.NearestNeighbor;
                g.PixelOffsetMode = PixelOffsetMode.Half;
                g.DrawImage(baseImage, 0, 0, drawerSide.Size.Width, drawerSide.Size.Height);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 0, 16, 1), scaledRect(trimImage, 0, 0, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 15, 16, 1), scaledRect(trimImage, 0, 15, 16, 1), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 0, 1, 1, 14), scaledRect(trimImage, 0, 1, 1, 14), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 15, 1, 1, 14), scaledRect(trimImage, 15, 1, 1, 14), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 1, 7, 14, 2), scaledRect(trimImage, 1, 7, 14, 2), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 7, 1, 2, 6), scaledRect(trimImage, 7, 1, 2, 6), GraphicsUnit.Pixel);
                g.DrawImage(trimImage, scaledRect(drawerSide, 7, 9, 2, 6), scaledRect(trimImage, 7, 9, 2, 6), GraphicsUnit.Pixel);

                g.FillRectangle(handleBrush, scaledRect(drawerFront1, 3, 1, 2, 1));
                g.FillRectangle(handleBrush, scaledRect(drawerFront1, 11, 1, 2, 1));
                g.FillRectangle(handleBrush, scaledRect(drawerFront1, 3, 9, 2, 1));
                g.FillRectangle(handleBrush, scaledRect(drawerFront1, 11, 9, 2, 1));

                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 1, 6, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 9, 1, 6, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 9, 6, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 9, 9, 6, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 2, 1, 4));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 6, 2, 1, 4));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 9, 2, 1, 4));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 14, 2, 1, 4));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 10, 1, 4));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 6, 10, 1, 4));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 9, 10, 1, 4));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 14, 10, 1, 4));

                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 7, 7, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 9, 7, 7, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 15, 7, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 9, 15, 7, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 7, 1, 1, 6));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 15, 1, 1, 6));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 7, 9, 1, 6));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 15, 9, 1, 6));
            }
        }

        private void compileTrim ()
        {
            drawerTrim = new Bitmap(baseImage.Size.Width, baseImage.Size.Height, PixelFormat.Format32bppArgb);
            using (Graphics g = Graphics.FromImage(drawerTrim)) {
                g.InterpolationMode = InterpolationMode.NearestNeighbor;
                g.PixelOffsetMode = PixelOffsetMode.Half;
                g.DrawImage(trimImage, 0, 0, drawerSide.Size.Width, drawerSide.Size.Height);
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 1, 15, 15, 1));
                g.FillRectangle(shadeBrush, scaledRect(drawerFront1, 15, 1, 1, 14));
            }
        }

        private Rectangle scaledRect (Bitmap refImage, int x, int y, int w, int h)
        {
            float scaleX = refImage.Size.Width / 16f;
            float scaleY = refImage.Size.Height / 16f;

            return new Rectangle((int)(scaleX * x), (int)(scaleY * y), (int)(scaleX * w), (int)(scaleY * h));
        }

        private void compileImage ()
        {
            if (baseImage == null || trimImage == null) {
                pictureCompiled.Image = null;
                return;
            }

            compileSide();
            compileSideH();
            compileSideV();
            compileSort();
            compileFront1();
            compileFront2();
            compileFront4();
            compileTrim();

            compiledImage = new Bitmap(256, 128, PixelFormat.Format32bppArgb);
            using (Graphics g = Graphics.FromImage(compiledImage)) {
                g.InterpolationMode = InterpolationMode.NearestNeighbor;
                g.PixelOffsetMode = PixelOffsetMode.Half;
                g.DrawImage(drawerSide, new Rectangle(0, 0, 64, 64));
                g.DrawImage(drawerSideH, new Rectangle(64, 0, 64, 64));
                g.DrawImage(drawerSideV, new Rectangle(128, 0, 64, 64));
                g.DrawImage(drawerSort, new Rectangle(192, 0, 64, 64));
                g.DrawImage(drawerFront1, new Rectangle(0, 64, 64, 64));
                g.DrawImage(drawerFront2, new Rectangle(64, 64, 64, 64));
                g.DrawImage(drawerFront4, new Rectangle(128, 64, 64, 64));
                g.DrawImage(drawerTrim, new Rectangle(192, 64, 64, 64));
            }

            pictureCompiled.Image = compiledImage;
        }

        private void button1_Click (object sender, EventArgs e)
        {
            OpenFileDialog openFileDialog1 = new OpenFileDialog() {
                Filter = "Image files (*.png)|*.png",
                RestoreDirectory = true,
            };

            if (openFileDialog1.ShowDialog() == DialogResult.OK) {
                using (Stream fileStream = openFileDialog1.OpenFile()) {
                    baseImage = (Bitmap)Bitmap.FromStream(fileStream);

                    Bitmap dest = new Bitmap(64, 64, System.Drawing.Imaging.PixelFormat.Format32bppArgb);
                    using (Graphics g = Graphics.FromImage(dest)) {
                        g.InterpolationMode = System.Drawing.Drawing2D.InterpolationMode.NearestNeighbor;
                        g.PixelOffsetMode = System.Drawing.Drawing2D.PixelOffsetMode.Half;
                        g.DrawImage(baseImage, 0, 0, 64, 64);
                    }
                    pictureBase.Image = dest;
                }
            }

            compileImage();
        }

        private void buttonTrim_Click (object sender, EventArgs e)
        {
            OpenFileDialog openFileDialog1 = new OpenFileDialog() {
                Filter = "Image files (*.png)|*.png",
                RestoreDirectory = true,
            };

            if (openFileDialog1.ShowDialog() == DialogResult.OK) {
                using (Stream fileStream = openFileDialog1.OpenFile()) {
                    trimImage = (Bitmap)Bitmap.FromStream(fileStream);

                    Bitmap dest = new Bitmap(64, 64, System.Drawing.Imaging.PixelFormat.Format32bppArgb);
                    using (Graphics g = Graphics.FromImage(dest)) {
                        g.InterpolationMode = System.Drawing.Drawing2D.InterpolationMode.NearestNeighbor;
                        g.PixelOffsetMode = System.Drawing.Drawing2D.PixelOffsetMode.Half;
                        g.DrawImage(trimImage, 0, 0, 64, 64);
                    }
                    pictureTrim.Image = dest;
                }
            }

            compileImage();
        }

        private void export_Click (object sender, EventArgs e)
        {
            if (String.IsNullOrWhiteSpace(textName.Text))
                return;

            try {
                drawerSide.Save("drawers_" + textName.Text + "_side.png", ImageFormat.Png);
                drawerSideH.Save("drawers_" + textName.Text + "_side_h.png", ImageFormat.Png);
                drawerSideV.Save("drawers_" + textName.Text + "_side_v.png", ImageFormat.Png);
                drawerSort.Save("drawers_" + textName.Text + "_sort.png", ImageFormat.Png);
                drawerFront1.Save("drawers_" + textName.Text + "_front_1.png", ImageFormat.Png);
                drawerFront2.Save("drawers_" + textName.Text + "_front_2.png", ImageFormat.Png);
                drawerFront4.Save("drawers_" + textName.Text + "_front_4.png", ImageFormat.Png);
                drawerTrim.Save("drawers_" + textName.Text + "_trim.png", ImageFormat.Png);
            }
            catch { }
        }
    }
}
