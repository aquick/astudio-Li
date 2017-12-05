/*****************************************************************************
 *   Copyright 2017, 2017 Andy Quick
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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Asynchronous task to compute the logarithmic integral function, Li(x).
 *
 */
public class Li_Task extends AsyncTask<Long, Integer, Integer>
{

    private static final String TAG = "Li_Task";
    private UI uinterface;
    private Context context;
    private StringBuffer result;
    private long argument;
    private long elapsedTime;
    private long iterations;
    private static final double li_at_2 = 1.0451637801174;
    private double Li_at_x;
    private double li_at_x;

    /**
     * Constructor: initialize member variables, get precision from UI, initialize constants.
     */
    public Li_Task(UI ui, Context ctx)
        throws Exception
    {
	this.uinterface = ui;
	this.context = ctx;
	this.result = new StringBuffer();
        this.argument = 2;
        this.elapsedTime = -1;
        this.iterations = 0;
    }

    private static int min(int x, int y)
    {
	return (x < y ? x : y);
    }

    private static double square_root_newton(double a, double epsilon)
    {
        double x0;
        double x1;

        if (a < 0.0) {
            throw new IllegalArgumentException();
        }
        if (a < epsilon) {
            return 0.0;
        }
        x0 = 1.0;
        for (;;) {
            x1 = (x0 + a/x0)/2.0;
            if (Math.abs(x1 - x0) < epsilon) {
                break;
            }
            x0 = x1;
        }
        return x1;
    }

    /**
     * Do the work of computing Li(x) for x >= 2.
     */
    protected Integer doInBackground(Long... params)
    {
        int rc = -1;
        long t1;
        long t2;
        
        if (params.length > 0) {
            this.argument = params[0].longValue();
        }
        try {
            double x = 0.0;
            double y;
            double yy;
            double z;
            double zz;
            double u;
            double v;
            double t;
            double y0;
            double h;
            double hby4;
            double x1;
            double x2;
            double x3;
            double x4;
            double arg = (double)this.argument;
            long n = 0;
            int m = 4;

            if (arg < 2.0) {
                throw new Exception("argument must be at least 2.0");
            }
            rc = 0;
            t1 = System.currentTimeMillis();
            x = 1.0;
            h = 1.0/m;
            y = 0.0;
            for (int i = 0; i < m; i++) {
                y = y + h*(1.0/x + 4.0/(x + h/2.0) + 1.0/(x + h))/6.0;
                x += h;
            }
            // here y is an approximation to ln(2) by Simpson's rule, and x == 2.0
            t = 0.0;
            for (;;) {
                // t is an approximation of Li(x)
                if (x >= arg) break;
                n++;
                if (n%1000 == 0) {
                    publishProgress(new Integer((int)(n/1000)));
                }
                //h = Math.sqrt(x);
                h = square_root_newton(x, x/1000000.0); // to 6 significant digits
                if (x + h > arg) {
                    h = arg - x;
                }
                y0 = y; // y0 is an approximation to ln(x)
                hby4 = h/4.0;
                x1 = x + hby4;
                x2 = x1 + hby4;
                x3 = x2 + hby4;
                x4 = x3 + hby4;
                u = 1.0/x2; // u is 1/(x+h/2)
                zz = 1.0/x + 4.0/x1 + u;
                yy = y0 + h*zz/12.0;
                // here yy is an approximation to ln(x + h/2)
                // Simpson's Rule: ln(x+h/2) ~ ln(x) + [h/12][1/x + 4/(x+h/4) + 1/(x+h/2)]
                z = u + 4.0/x3 + 1.0/x4;
                y = yy + h*z/12.0;
                // here y is an approximation to ln(x + h)
                // Simpson's Rule: ln(x+h) ~ ln(x+h/2) + [h/12][1/(x+h/2) + 4/(x+3h/4) + 1/(x+h)]
                v = 1.0/y0 + 4.0/yy + 1.0/y;
                t = t + h*v/6.0;
                // here t is an approximation to Li(x+h)
                // Simpson's Rule: Li(x+h) ~ Li(x) + [h/6][1/ln(x) + 4/ln(x+h/2) + 1/ln(x+h)]
		// For h=sqrt(x), |error| < 14 / 90*32*sqrt(x)*ln(x)*ln(x)
                x += h;
            }
            rc = 0;
            this.iterations = n;
            t2 = System.currentTimeMillis();
            this.elapsedTime = t2 - t1;
            this.result.append(t);
            this.Li_at_x = t;
            this.li_at_x = t + li_at_2;
        }
        catch (Exception e) {
            this.result.append(e.toString());
            /***
             StackTraceElement[] st = e.getStackTrace();
             for (int m = 0; m < st.length; m++) {
             result.append("\n");
             result.append(st[m].toString());
             }
             ***/
            rc = -1;
        }
        return Integer.valueOf(rc);
    }
    
    /**
     * Post-execution work.
     */
    protected void onPostExecute(Integer result)
    {
        if (this.result.length() > 0) {
            if(result.intValue() == 0) {
                StringBuffer sb = new StringBuffer();
                sb.append("Li(");
                sb.append(this.argument);
                sb.append(")=");
                sb.append(this.result.toString());
                sb.append("\n");
                sb.append("li(");
                sb.append(this.argument);
                sb.append(")=");
                sb.append(this.li_at_x);
                sb.append("\n");
                sb.append("number of iterations: ");
                sb.append(this.iterations);
                sb.append("\n");
                sb.append("elapsed time: ");
                sb.append(this.elapsedTime);
                sb.append(" milliseconds");
                uinterface.display(sb.toString());
                Log.d(TAG, sb.toString());
            } else {
                uinterface.display(this.result.toString());
                Log.d(TAG, this.result.toString());
            }
        }
    }
    
    /**
     * Pre-execution work.
     */
    protected void onPreExecute()
    {
        uinterface.display(TAG);
    }

    /**
     * Update the UI with progress.
     */
    protected void onProgressUpdate(Integer... progress)
    {
        int i = progress[0];
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        sb.append(Integer.toString(i));
        sb.append("] ");
        uinterface.display(sb.toString());
        Log.d(TAG, sb.toString());
    }

}
