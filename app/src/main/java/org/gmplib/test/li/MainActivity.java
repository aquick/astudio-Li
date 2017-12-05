/*****************************************************************************
 *   Copyright 2017 Andy Quick
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *****************************************************************************/
package org.gmplib.test.li;

import android.app.Activity;
//import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

/***
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.OutputStreamWriter;
 import java.util.zip.ZipInputStream;
 ***/


public class MainActivity extends Activity implements UI {

    private TextView mView;
    private TextView mArgument;
    private Button mStart;
    private Button mCancel;
    AsyncTask<Long, Integer, Integer> task = null;
    private long argument = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	mView = (TextView) findViewById(R.id.TextView01);
	mArgument = (TextView) findViewById(R.id.TextView03);
	mStart = (Button) findViewById(R.id.Button01);
	mCancel = (Button) findViewById(R.id.Button02);
	mCancel.setOnClickListener(
		new View.OnClickListener() {
		    public void onClick(View v)
		    {
			MainActivity.this.task.cancel(false);
		    }
		});
	mStart.setOnClickListener(
		new View.OnClickListener() {
		    public void onClick(View v)
		    {
			try {
			    MainActivity.this.mView.setText("");
			    StringBuffer sb = new StringBuffer();
			    sb.append(MainActivity.this.mArgument.getText());
			    try {
				MainActivity.this.argument = Long.parseLong(sb.toString());
			    }
			    catch (NumberFormatException e) {
			    }
			    task = new Li_Task(MainActivity.this, MainActivity.this);
			    task.execute(MainActivity.this.argument);
			}
			catch (Exception e) {
			    Log.d("Li_Task", "MainActivity.Start.onClick: " + "Exception " + e.getMessage());
			}
		    }
		});
	try {
	}
	catch (Exception e) {
	    Log.d("Li_Task", "MainActivity.onCreate: " + e.toString());
	}
    }

    public void display(String line)
    {
	mView.append(line);
	mView.append("\n");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	return super.onOptionsItemSelected(item);
    }
}
