package com.ayush.passwordgenerator;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    int length;
    private InterstitialAd mInterstitialAd;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView passwordTextView = findViewById(R.id.password_text);
        EditText editText = findViewById(R.id.edit_query);
        CheckBox uppercaseCheckBox = findViewById(R.id.checkBoxUpper);
        CheckBox lowercaseCheckBox = findViewById(R.id.checkBoxLower);
        CheckBox digitCheckBox = findViewById(R.id.checkBoxDigits);
        CheckBox symbolCheckBox = findViewById(R.id.checkBoxSymbols);
        Button button = findViewById(R.id.button);

        MobileAds.initialize(this, initializationStatus -> {
        });
        interstitial();

        //On Clicking Generate Button
        button.setOnClickListener(v -> {
            int ch=0;
            String editTextstr = editText.getText().toString();

            if(!editTextstr.equals("")) { //if not empty then only parse to avoid NumberFormatException
                length = Integer.parseInt(editTextstr);
            }

            if(String.valueOf(editText.getText()).equals(""))
            {
                Toast.makeText(this, "Give Size of Password!", Toast.LENGTH_SHORT).show();
            }
            else if(length>1000)
            {
                Toast.makeText(this,"Max Password Size allowed is 1000!",Toast.LENGTH_SHORT).show();
            }
            else if(length<0)
            {
                Toast.makeText(this,"Negative Integer! Kindly check your input.",Toast.LENGTH_SHORT).show();
            }
            else if (length==0)
            {
                Toast.makeText(this,"Size of password cannot be 0!",Toast.LENGTH_SHORT).show();
            }
            else{


                //Checking which checkboxes are checked and sending ch value accordingly

                if(uppercaseCheckBox.isChecked() &&  lowercaseCheckBox.isChecked() && digitCheckBox.isChecked() && symbolCheckBox.isChecked())
                    ch=1;
                else if(uppercaseCheckBox.isChecked() && lowercaseCheckBox.isChecked() && !digitCheckBox.isChecked() && !symbolCheckBox.isChecked())
                    ch=2;
                else if(uppercaseCheckBox.isChecked() && !lowercaseCheckBox.isChecked() && digitCheckBox.isChecked() && !symbolCheckBox.isChecked())
                    ch=3;
                else if(uppercaseCheckBox.isChecked() && !lowercaseCheckBox.isChecked() && !digitCheckBox.isChecked() && symbolCheckBox.isChecked())
                    ch=4;
                else if(!uppercaseCheckBox.isChecked() && lowercaseCheckBox.isChecked() && digitCheckBox.isChecked() && !symbolCheckBox.isChecked())
                    ch=5;
                else if(!uppercaseCheckBox.isChecked() && lowercaseCheckBox.isChecked() && !digitCheckBox.isChecked() && symbolCheckBox.isChecked())
                    ch=6;
                else if(!uppercaseCheckBox.isChecked() && !lowercaseCheckBox.isChecked() && digitCheckBox.isChecked() && symbolCheckBox.isChecked())
                    ch=7;
                else if(uppercaseCheckBox.isChecked() && lowercaseCheckBox.isChecked() && digitCheckBox.isChecked() && !symbolCheckBox.isChecked())
                    ch=8;
                else if(uppercaseCheckBox.isChecked() && lowercaseCheckBox.isChecked() && !digitCheckBox.isChecked() && symbolCheckBox.isChecked())
                    ch=9;
                else if(uppercaseCheckBox.isChecked() && !lowercaseCheckBox.isChecked() && digitCheckBox.isChecked() && symbolCheckBox.isChecked())
                    ch=10;
                else if(!uppercaseCheckBox.isChecked() && lowercaseCheckBox.isChecked() && digitCheckBox.isChecked() && symbolCheckBox.isChecked())
                    ch=11;

                   if(ch!=0) { //execute only if any 2 or more checkboxes are checked

                       String password = String.valueOf(password(length, ch));
                       String strength = checkStrength(password);
                       passwordTextView.setText(password);// showing password
                       passwordTextView.setMovementMethod(new ScrollingMovementMethod());// making textView scrollable

                       TextView passwordStrength = findViewById(R.id.password_text_strength);

                       switch (strength) {
                           case "Strong":
                               passwordStrength.setText("Strong Password Strength!");
                               passwordStrength.setVisibility(View.VISIBLE);
                               passwordStrength.setTextColor(Color.rgb(0, 128, 0));
                               break;
                           case "Moderate":
                               passwordStrength.setText("Moderate Password Strength!");
                               passwordStrength.setVisibility(View.VISIBLE);
                               passwordStrength.setTextColor(Color.rgb(255, 69, 0));
                               break;
                           case "Weak":
                               passwordStrength.setText("Weak Password Strength!");
                               passwordStrength.setVisibility(View.VISIBLE);
                               passwordStrength.setTextColor(Color.RED);
                               break;
                           case "0":
                           // make the strength textView disappear when password length is 0
                               passwordStrength.setVisibility(View.GONE);
                               Toast.makeText(this, "Size of password cannot be 0!", Toast.LENGTH_SHORT).show();
                               break;
                       }

                       /*Button to copy the password to clipboard*/

                       ImageButton imageButton = findViewById(R.id.imageButtonCopy);
                       imageButton.setVisibility(View.VISIBLE);
                       imageButton.setOnClickListener(v1 -> {

                           ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                           ClipData clipData = ClipData.newPlainText("",password);
                           clipboardManager.setPrimaryClip(clipData);
                           Toast.makeText(this, "Copied The Password to your clipboard",Toast.LENGTH_SHORT).show();
                           imageButton.setVisibility(View.GONE);

                           //for interstitial ad


                           if (mInterstitialAd != null) {

                               mInterstitialAd.show(MainActivity.this);

                               mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                   @Override
                                   public void onAdDismissedFullScreenContent() {
                                       // Called when fullscreen content is dismissed.
                                      // startActivity(new Intent(MainActivity.this, EquationSolver.class));
                                       Log.d("TAG", "The ad was dismissed.");
                                   }

                                   @Override
                                   public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                       // Called when fullscreen content failed to show.
                                     //  startActivity(new Intent(MainActivity.this, EquationSolver.class));
                                       Log.d("TAG", "The ad failed to show.");
                                   }

                                   @Override
                                   public void onAdShowedFullScreenContent() {
                                       // Called when fullscreen content is shown.
                                       // Make sure to set your reference to null so you don't
                                       // show it a second time.

                                       mInterstitialAd = null;
                                       Log.d("TAG", "The ad was shown.");
                                   }
                               });
                           }



                       });


                   }
                   else
                   {
                       Toast.makeText(getApplicationContext(), "Select At least 2 options to make a strong password!", Toast.LENGTH_SHORT).show();
                   }
            }
            });
    }

    public static char[] password(int length,int choice) //function to generate random password
    {
        String UpperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String LowerCase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols ="!@#$%^&*_=+-/.?)";
        String combine = "";
        if(choice==1)
            combine =UpperCase+LowerCase+digits+symbols;
        else if(choice==2)
            combine=UpperCase+LowerCase;
        else if(choice==3)
            combine =UpperCase+digits;
        else if(choice == 4)
            combine=UpperCase+symbols;
        else if(choice==5)
            combine=LowerCase+digits;
        else if(choice==6)
            combine=LowerCase+symbols;
        else if(choice==7)
            combine=digits+symbols;
        else if(choice==8)
            combine=UpperCase+LowerCase+digits;
        else if(choice==9)
            combine=UpperCase+LowerCase+symbols;
        else if(choice==10)
            combine=UpperCase+digits+symbols;
        else if(choice == 11)
            combine=LowerCase+digits+symbols;

        Random random = new Random();
        char[] password = new char[length];

        for(int i=0;i<length;i++)
        {
            password[i] = combine.charAt(random.nextInt(combine.length()));
        }
        return password;

    }

    public static String checkStrength(String password){
        int length = password.length();
        boolean hasUpper = false,hasLower=false,hasDigits=false,hasSpecial=false;
        Set<Character> set =new HashSet<Character>(Arrays.asList('!', '@', '#', '$', '%', '^', '&',
                '*', '(', ')', '-', '+'));

        for(char i : password.toCharArray())
        {
            if(Character.isLowerCase(i))
                hasLower=true;
            if(Character.isUpperCase(i))
                hasUpper=true;
            if(Character.isDigit(i))
                hasDigits=true;
            if(set.contains(i))
                hasSpecial=true;
        }

        if(hasDigits && hasLower && hasSpecial && hasUpper && (length>=10))
            return "Strong";
        else if((hasLower||hasSpecial||hasUpper) && (length>=6))
        return "Moderate";
        else if(length==0)
            return "0";
        else
            return "Weak";
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.menu_tip:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Tip For Strong Password:");
                builder.setMessage(R.string.tips);
                builder.setIcon(R.drawable.tips);
                builder.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.menu_about:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("About:");
                builder1.setMessage(R.string.about);
                builder1.setIcon(R.drawable.about);
                builder1.setPositiveButton("OK", (dialog, which) -> dialog.cancel());
                AlertDialog alertDialog1 = builder1.create();
                alertDialog1.show();
                break;
            case R.id.menu_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/developer?id=Ayush+Ojha");
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent,null);
                startActivity(shareIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void interstitial(){

        AdRequest adRequest = new AdRequest.Builder().build();
    InterstitialAd.load(this,"ca-app-pub-9268031946750579/2588202958zaq" , adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }
}