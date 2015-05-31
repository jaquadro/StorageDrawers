namespace TextureBuilder
{
    partial class Form1
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose (bool disposing)
        {
            if (disposing && (components != null)) {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent ()
        {
            this.pictureCompiled = new System.Windows.Forms.PictureBox();
            this.pictureTrim = new System.Windows.Forms.PictureBox();
            this.pictureBase = new System.Windows.Forms.PictureBox();
            this.buttonBase = new System.Windows.Forms.Button();
            this.buttonTrim = new System.Windows.Forms.Button();
            this.export = new System.Windows.Forms.Button();
            this.textName = new System.Windows.Forms.TextBox();
            ((System.ComponentModel.ISupportInitialize)(this.pictureCompiled)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureTrim)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBase)).BeginInit();
            this.SuspendLayout();
            // 
            // pictureCompiled
            // 
            this.pictureCompiled.BackColor = System.Drawing.SystemColors.ControlDark;
            this.pictureCompiled.Location = new System.Drawing.Point(12, 111);
            this.pictureCompiled.Name = "pictureCompiled";
            this.pictureCompiled.Size = new System.Drawing.Size(256, 128);
            this.pictureCompiled.TabIndex = 0;
            this.pictureCompiled.TabStop = false;
            // 
            // pictureTrim
            // 
            this.pictureTrim.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.pictureTrim.Location = new System.Drawing.Point(82, 12);
            this.pictureTrim.Name = "pictureTrim";
            this.pictureTrim.Size = new System.Drawing.Size(64, 64);
            this.pictureTrim.TabIndex = 1;
            this.pictureTrim.TabStop = false;
            // 
            // pictureBase
            // 
            this.pictureBase.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.pictureBase.Location = new System.Drawing.Point(12, 12);
            this.pictureBase.Name = "pictureBase";
            this.pictureBase.Size = new System.Drawing.Size(64, 64);
            this.pictureBase.TabIndex = 2;
            this.pictureBase.TabStop = false;
            // 
            // buttonBase
            // 
            this.buttonBase.Location = new System.Drawing.Point(12, 82);
            this.buttonBase.Name = "buttonBase";
            this.buttonBase.Size = new System.Drawing.Size(64, 23);
            this.buttonBase.TabIndex = 3;
            this.buttonBase.Text = "Base";
            this.buttonBase.UseVisualStyleBackColor = true;
            this.buttonBase.Click += new System.EventHandler(this.button1_Click);
            // 
            // buttonTrim
            // 
            this.buttonTrim.Location = new System.Drawing.Point(82, 82);
            this.buttonTrim.Name = "buttonTrim";
            this.buttonTrim.Size = new System.Drawing.Size(64, 23);
            this.buttonTrim.TabIndex = 4;
            this.buttonTrim.Text = "Trim";
            this.buttonTrim.UseVisualStyleBackColor = true;
            this.buttonTrim.Click += new System.EventHandler(this.buttonTrim_Click);
            // 
            // export
            // 
            this.export.Location = new System.Drawing.Point(204, 82);
            this.export.Name = "export";
            this.export.Size = new System.Drawing.Size(64, 23);
            this.export.TabIndex = 5;
            this.export.Text = "Export";
            this.export.UseVisualStyleBackColor = true;
            this.export.Click += new System.EventHandler(this.export_Click);
            // 
            // textName
            // 
            this.textName.Location = new System.Drawing.Point(152, 56);
            this.textName.Name = "textName";
            this.textName.Size = new System.Drawing.Size(116, 20);
            this.textName.TabIndex = 6;
            // 
            // Form1
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(282, 252);
            this.Controls.Add(this.textName);
            this.Controls.Add(this.export);
            this.Controls.Add(this.buttonTrim);
            this.Controls.Add(this.buttonBase);
            this.Controls.Add(this.pictureBase);
            this.Controls.Add(this.pictureTrim);
            this.Controls.Add(this.pictureCompiled);
            this.Name = "Form1";
            this.Text = "Form1";
            ((System.ComponentModel.ISupportInitialize)(this.pictureCompiled)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureTrim)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBase)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.PictureBox pictureCompiled;
        private System.Windows.Forms.PictureBox pictureTrim;
        private System.Windows.Forms.PictureBox pictureBase;
        private System.Windows.Forms.Button buttonBase;
        private System.Windows.Forms.Button buttonTrim;
        private System.Windows.Forms.Button export;
        private System.Windows.Forms.TextBox textName;
    }
}

