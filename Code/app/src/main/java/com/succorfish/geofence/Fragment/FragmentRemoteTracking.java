package com.succorfish.geofence.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.succorfish.geofence.BaseFragment.BaseFragment;
import com.succorfish.geofence.CustomObjectsAPI.VoVessel;
import com.succorfish.geofence.MainActivity;
import com.succorfish.geofence.R;
import com.succorfish.geofence.adapter.FragmentHistoryAdapter;
import com.succorfish.geofence.adapter.FragmentRemoteTrackAdapter;
import com.succorfish.geofence.dialog.DialogProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.succorfish.geofence.utility.RetrofitHelperClass.haveInternet;

public class FragmentRemoteTracking extends BaseFragment {
    View fragmentRemoteTrackingView;
    private Unbinder unbinder;
    MainActivity mainActivity;
    private ArrayList<VoVessel> mVoVesselListTemp = new ArrayList<>();
    @BindView(R.id.fragment_remoteTracking_RecycleView)
    RecyclerView fragmentRemoteTrackingRecyclerView;
    FragmentRemoteTrackAdapter fragmentRemoteTrackAdapter;
    private KProgressHUD hud;
    DialogProvider dialogProvider;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVoVesselListTemp = new ArrayList<>();
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRemoteTrackingView = inflater.inflate(R.layout.fragment_remote_tracking, container, false);
        unbinder = ButterKnife.bind(this, fragmentRemoteTrackingView);
        intializeView();
        intilializeDialogProvider();
        setUpRecycleView();
        if (haveInternet(getActivity())) {
            getVesselAssetList();
        }else {
            dialogProvider.errorDialog("NO Internet");
        }
        return fragmentRemoteTrackingView;
    }

    private void intializeView(){
        hud = KProgressHUD.create(getActivity());
    }
    private void intilializeDialogProvider(){
        dialogProvider = new DialogProvider(getActivity());
    }
    private void showProgressDialog(String label) {
        hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(label);
        hud.show();
    }
    private void cancelProgressDialog(){
        if (hud != null) {
            if (hud.isShowing()) {
                hud.dismiss();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void setUpRecycleView() {
        fragmentRemoteTrackAdapter = new FragmentRemoteTrackAdapter(mVoVesselListTemp);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        fragmentRemoteTrackingRecyclerView.setLayoutManager(mLayoutManager);
        fragmentRemoteTrackingRecyclerView.setAdapter(fragmentRemoteTrackAdapter);
    }

    public void getVesselAssetList() {
        showProgressDialog("Loading");
        Map<String, Object> jsonParams = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            jsonParams = new ArrayMap<>();
        } else {

        }
        jsonParams.put("filters", "");
        jsonParams.put("jsonFunction", "");
        jsonParams.put("value", "");
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(jsonParams)).toString());

        Call<String> mLogin = mainActivity.mApiService.getAllAssetList("OBJECT", body);
        System.out.println("URL-" + mLogin.request().url().toString());
        mLogin.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                    Gson gson = new Gson();
                    if (response.code() == 200 || response.isSuccessful()) {
                        mVoVesselListTemp = new ArrayList<>();
                        System.out.println("response mVesselData---------" + response.body());
                        try {
                            Object json = new JSONTokener(response.body().toString()).nextValue();
                            if (json instanceof JSONObject) {
                                System.out.println("JSON_OBJECT");
                            } else if (json instanceof JSONArray) {
                                System.out.println("JSONArray");
                                TypeToken<List<VoVessel>> token = new TypeToken<List<VoVessel>>() {
                                };
                                List<VoVessel> mVoVesselListResponse = gson.fromJson(response.body(), token.getType());
                                if (mVoVesselListResponse != null) {
                                    if (mVoVesselListResponse.size() > 0) {
                                        mVoVesselListTemp.addAll(mVoVesselListResponse);
                                        fragmentRemoteTrackAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else if (response.code() == 401) {
                        cancelProgressDialog();
                        dialogProvider.errorDialog("UNAUTHORIZED TOKEN");
                        /**
                         * ForLogout for the User and Navigate to MainActivity...
                         */
                    } else {
                        /**
                         * Show Server Eror.
                         */
                        dialogProvider.errorDialog("Server Eror");
                    }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }
}
