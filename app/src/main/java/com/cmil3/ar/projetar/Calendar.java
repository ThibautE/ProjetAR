    package com.cmil3.ar.projetar;
    import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.R.attr.id;
import static com.cmil3.ar.projetar.R.id.bottom;
import static com.cmil3.ar.projetar.R.id.menuToolbar;
import static com.cmil3.ar.projetar.R.id.wrap_content;

    public class Calendar extends Activity implements EasyPermissions.PermissionCallbacks {
        GoogleAccountCredential mCredential;
        private TextView mOutputText;
        private Button buttonENT;
        private Button buttonUM;
        ProgressDialog mProgress;
        private Toolbar menu;

        static final int REQUEST_ACCOUNT_PICKER = 1000;
        static final int REQUEST_AUTHORIZATION = 1001;
        static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
        static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

        private static final String PREF_ACCOUNT_NAME = "accountName";
        private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };


        //initiate tool bar with buttons and actions
        private void initToolbar() {
            Toolbar toolbarBottom = menu;
                toolbarBottom.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_main:
                                Intent calen = new Intent(Calendar.this, MainActivity.class);
                                startActivity(calen);
                                break;
                            case R.id.action_carte:
                                Intent carte = new Intent(Calendar.this, Carte.class);
                                startActivity(carte);
                                break;
                            case R.id.action_addPoi:
                                Intent addPoi = new Intent(Calendar.this, AddPoi.class);
                                startActivity(addPoi);
                                break;
                            case R.id.action_galerie:
                                Uri gal = Uri.parse("http://www.lirmm.fr/campusar/galerie/");
                                Intent intent_gal = new Intent(Intent.ACTION_VIEW, gal);
                                startActivity(intent_gal);
                                break;
                            case R.id.action_calendar:
                                break;
                        }
                        return true;
                    }
                });
                // Inflate a menu to be displayed in the toolbar
                toolbarBottom.inflateMenu(R.menu.menumain);
        }


        /**
         * Create the main activity.
         * @param savedInstanceState previously saved instance data.
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            LinearLayout activityLayout = new LinearLayout(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            activityLayout.setLayoutParams(lp);
            activityLayout.setOrientation(LinearLayout.VERTICAL);
            activityLayout.setPadding(16, 16, 16, 16);

            ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);


            mOutputText = new TextView(this);
            mOutputText.setLayoutParams(tlp);
            mOutputText.setPadding(16, 16, 16, 16);
            mOutputText.setVerticalScrollBarEnabled(true);
            mOutputText.setMovementMethod(new ScrollingMovementMethod());
            mOutputText.setText(
                    "Sélectionnez le compte Google associé à votre agenda.");


            mProgress = new ProgressDialog(this);
            mProgress.setMessage("Attente de Google Calendar...");


            ///////////////////////////ajout de boutons liens vers les sites de l'UM et de l'ENT
            //ENT
            buttonENT = new Button(this);
            buttonENT.setText("ENT");
            buttonENT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse("https://ent.umontpellier.fr"));
                        startActivity(intent);
                }
            });

            //UM
            buttonUM = new Button(this);
            buttonUM.setText("Université Montpellier");
            buttonUM.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse("http://www.umontpellier.fr/"));
                    startActivity(intent);
                }
            });
            /////////////////////////////////////////////////////////////////////////

            ///////////////////////////////Ajout du menu
            menu = new Toolbar(this);
            menu.setId(id/menuToolbar);
            menu.setMinimumWidth(wrap_content);
            menu.setMinimumHeight(wrap_content);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {menu.setForegroundGravity(bottom);}
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {menu.setElevation(15);}

            /////////////////////////////////////////////////////////////////////////

            initToolbar();
            activityLayout.addView(mOutputText);
            activityLayout.addView(buttonENT);
            activityLayout.addView(buttonUM);
            activityLayout.addView(menu);

            setContentView(activityLayout);

            // Initialize credentials and service object.
            mCredential = GoogleAccountCredential.usingOAuth2(
                    getApplicationContext(), Arrays.asList(SCOPES))
                    .setBackOff(new ExponentialBackOff());

            getResultsFromApi();

        }

        /**
         * Attempt to call the API, after verifying that all the preconditions are
         * satisfied. The preconditions are: Google Play Services installed, an
         * account was selected and the device currently has online access. If any
         * of the preconditions are not satisfied, the app will prompt the user as
         * appropriate.
         */
        private void getResultsFromApi() {
            if (! isGooglePlayServicesAvailable()) {
                acquireGooglePlayServices();
            } else if (mCredential.getSelectedAccountName() == null) {
                chooseAccount();
            } else if (! isDeviceOnline()) {
                mOutputText.setText("Connexion à internet nécessaire.");
            } else {
                new MakeRequestTask(mCredential).execute();
            }
        }

        /**
         * Attempts to set the account used with the API credentials. If an account
         * name was previously saved it will use that one; otherwise an account
         * picker dialog will be shown to the user. Note that the setting the
         * account to use with the credentials object requires the app to have the
         * GET_ACCOUNTS permission, which is requested here if it is not already
         * present. The AfterPermissionGranted annotation indicates that this
         * function will be rerun automatically whenever the GET_ACCOUNTS permission
         * is granted.
         */
        @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
        private void chooseAccount() {
            if (EasyPermissions.hasPermissions(
                    this, Manifest.permission.GET_ACCOUNTS)) {
                String accountName = getPreferences(Context.MODE_PRIVATE)
                        .getString(PREF_ACCOUNT_NAME, null);
                if (accountName != null) {
                    mCredential.setSelectedAccountName(accountName);
                    getResultsFromApi();
                } else {
                    // Start a dialog from which the user can choose an account
                    startActivityForResult(
                            mCredential.newChooseAccountIntent(),
                            REQUEST_ACCOUNT_PICKER);
                }
            } else {
                // Request the GET_ACCOUNTS permission via a user dialog
                EasyPermissions.requestPermissions(
                        this,
                        "L'agenda nécessite l'accès à votre compte Google.",
                        REQUEST_PERMISSION_GET_ACCOUNTS,
                        Manifest.permission.GET_ACCOUNTS);
            }
        }

        /**
         * Called when an activity launched here (specifically, AccountPicker
         * and authorization) exits, giving you the requestCode you started it with,
         * the resultCode it returned, and any additional data from it.
         * @param requestCode code indicating which activity result is incoming.
         * @param resultCode code indicating the result of the incoming
         *     activity result.
         * @param data Intent (containing result data) returned by incoming
         *     activity result.
         */
        @Override
        protected void onActivityResult(
                int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch(requestCode) {
                case REQUEST_GOOGLE_PLAY_SERVICES:
                    if (resultCode != RESULT_OK) {
                        mOutputText.setText( "Cette application nécessite l'installation de Google Play. " +
                                "Installez la dernière version de Google Play puis relancez l'application.");
                    } else {
                        getResultsFromApi();
                    }
                    break;
                case REQUEST_ACCOUNT_PICKER:
                    if (resultCode == RESULT_OK && data != null &&
                            data.getExtras() != null) {
                        String accountName =
                                data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                        if (accountName != null) {
                            SharedPreferences settings =
                                    getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(PREF_ACCOUNT_NAME, accountName);
                            editor.apply();
                            mCredential.setSelectedAccountName(accountName);
                            getResultsFromApi();
                        }
                    }
                    break;
                case REQUEST_AUTHORIZATION:
                    if (resultCode == RESULT_OK) {
                        getResultsFromApi();
                    }
                    break;
            }
        }

        /**
         * Respond to requests for permissions at runtime for API 23 and above.
         * @param requestCode The request code passed in
         *     requestPermissions(android.app.Activity, String, int, String[])
         * @param permissions The requested permissions. Never null.
         * @param grantResults The grant results for the corresponding permissions
         *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
         */
        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            EasyPermissions.onRequestPermissionsResult(
                    requestCode, permissions, grantResults, this);
        }

        /**
         * Callback for when a permission is granted using the EasyPermissions
         * library.
         * @param requestCode The request code associated with the requested
         *         permission
         * @param list The requested permission list. Never null.
         */
        @Override
        public void onPermissionsGranted(int requestCode, List<String> list) {
            // Do nothing.
        }

        /**
         * Callback for when a permission is denied using the EasyPermissions
         * library.
         * @param requestCode The request code associated with the requested
         *         permission
         * @param list The requested permission list. Never null.
         */
        @Override
        public void onPermissionsDenied(int requestCode, List<String> list) {
            // Do nothing.
        }

        /**
         * Checks whether the device currently has a network connection.
         * @return true if the device has a network connection, false otherwise.
         */
        private boolean isDeviceOnline() {
            ConnectivityManager connMgr =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }

        /**
         * Check that Google Play services APK is installed and up to date.
         * @return true if Google Play Services is available and up to
         *     date on this device; false otherwise.
         */
        private boolean isGooglePlayServicesAvailable() {
            GoogleApiAvailability apiAvailability =
                    GoogleApiAvailability.getInstance();
            final int connectionStatusCode =
                    apiAvailability.isGooglePlayServicesAvailable(this);
            return connectionStatusCode == ConnectionResult.SUCCESS;
        }

        /**
         * Attempt to resolve a missing, out-of-date, invalid or disabled Google
         * Play Services installation via a user dialog, if possible.
         */
        private void acquireGooglePlayServices() {
            GoogleApiAvailability apiAvailability =
                    GoogleApiAvailability.getInstance();
            final int connectionStatusCode =
                    apiAvailability.isGooglePlayServicesAvailable(this);
            if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
                showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            }
        }


        /**
         * Display an error dialog showing that Google Play Services is missing
         * or out of date.
         * @param connectionStatusCode code describing the presence (or lack of)
         *     Google Play Services on this device.
         */
        void showGooglePlayServicesAvailabilityErrorDialog(
                final int connectionStatusCode) {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            Dialog dialog = apiAvailability.getErrorDialog(
                    Calendar.this,
                    connectionStatusCode,
                    REQUEST_GOOGLE_PLAY_SERVICES);
            dialog.show();
        }

        /**
         * An asynchronous task that handles the Google Calendar API call.
         * Placing the API calls in their own task ensures the UI stays responsive.
         */
        private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
            private com.google.api.services.calendar.Calendar mService = null;
            private Exception mLastError = null;

            MakeRequestTask(GoogleAccountCredential credential) {
                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                mService = new com.google.api.services.calendar.Calendar.Builder(
                        transport, jsonFactory, credential)
                        .setApplicationName("Google Calendar API Android Quickstart")
                        .build();
            }

            /**
             * Background task to call Google Calendar API.
             * @param params no parameters needed for this task.
             */
            @Override
            protected List<String> doInBackground(Void... params) {
                try {
                    return getDataFromApi();
                } catch (Exception e) {
                    mLastError = e;
                    cancel(true);
                    return null;
                }
            }

            /**
             * Fetch a list of the next 10 events from the primary calendar.
             * @return List of Strings describing returned events.
             * @throws IOException
             */
            private List<String> getDataFromApi() throws IOException {
                // List the next 10 events from the primary calendar.
                DateTime now = new DateTime(System.currentTimeMillis());
                List<String> eventStrings = new ArrayList<String>();
                Events events = mService.events().list("primary")
                        .setMaxResults(10)
                        .setTimeMin(now)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();
                List<Event> items = events.getItems();

                for (Event event : items) {
                    DateTime start = event.getStart().getDateTime();
                    //if event doesn t have start time (all day)
                    if (start == null) {
                        start = event.getStart().getDate(); //use start date
                    }
                    String description = event.getDescription();
                    //if event doesn t have description
                    if (description == null) {
                        description = "";
                    }
                    String location = event.getLocation();
                    //if event doesn t have location
                    if(location==null){
                        location = "";
                    }

                    Date date=new Date(start.getValue());
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm");
                    String dt = df.format(date);

                    eventStrings.add(
                            String.format("%s: %s \n%s - %s\n\n", event.getSummary(), description, dt, location));
                }
                return eventStrings;
            }


            @Override
            protected void onPreExecute() {
                mOutputText.setText("");
                mProgress.show();
            }

            @Override
            protected void onPostExecute(List<String> output) {
                mProgress.hide();
                if (output == null || output.size() == 0) {
                    mOutputText.setText(Html.fromHtml("<b>Aucun évènement à venir dans votre agenda Google.</b>"));
                } else {
                    output.add(0, "VOTRE EMPLOI DU TEMPS:\n");
                    mOutputText.setText(TextUtils.join("\n", output));
                }
            }

            @Override
            protected void onCancelled() {
                mProgress.hide();
                if (mLastError != null) {
                    if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                        showGooglePlayServicesAvailabilityErrorDialog(
                                ((GooglePlayServicesAvailabilityIOException) mLastError)
                                        .getConnectionStatusCode());
                    } else if (mLastError instanceof UserRecoverableAuthIOException) {
                        startActivityForResult(
                                ((UserRecoverableAuthIOException) mLastError).getIntent(),
                                Calendar.REQUEST_AUTHORIZATION);
                    } else {
                        mOutputText.setText("L'erreur suivante est survenue:\n"
                                + mLastError.getMessage() + " "
                                + mLastError.getCause());
                    }
                } else {
                    mOutputText.setText("Requête annulée.");
                }
            }
        }
    }