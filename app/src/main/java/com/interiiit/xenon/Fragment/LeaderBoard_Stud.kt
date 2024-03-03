package com.interiiit.xenon.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.interiiit.xenon.Adapter.LeaderAdapter
import com.interiiit.xenon.databinding.FragmentLeaderBoardStudBinding
import com.google.android.material.snackbar.Snackbar
import com.interiiit.xenon.other.IIITSlogo
import org.json.JSONException

data class IIITData(
    val Name: String,
    val Points: Int,
)

class LeaderBoard_Stud : Fragment() {
    private lateinit var binding: FragmentLeaderBoardStudBinding
    private var userListNo3: MutableList<IIITData> = mutableListOf()
    private var top3iiitslist3: MutableList<IIITData> = mutableListOf()
    private lateinit var useAdapter: LeaderAdapter
    private var logo = IIITSlogo.logo
    private val leaderBoardURL = "https://app-admin-api.asmitaiiita.org/api/leaderboard/"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderBoardStudBinding.inflate(layoutInflater, container, false)
        binding.leaderRv.visibility = View.INVISIBLE
        binding.resLot.visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        useAdapter = LeaderAdapter(userListNo3,logo)
        binding.leaderRv.layoutManager = LinearLayoutManager(context)
        binding.leaderRv.adapter = useAdapter
        fetchFromURl()
        binding.refresh.setOnRefreshListener {
            fetchFromURl()
            Snackbar.make(binding.root, "Data refreshed", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun fetchFromURl() {
            userListNo3.clear()
            top3iiitslist3.clear()
            val url = leaderBoardURL
            val requestQueue = Volley.newRequestQueue(requireContext())
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                { response ->
                    try {
                        val top3List = mutableListOf<IIITData>()
                        val remainingList = mutableListOf<IIITData>()
                        val dataArray = response.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val jsonObject = dataArray.getJSONObject(i)
                            val name = jsonObject.getString("Name")
                            val points = jsonObject.getInt("Points")
                            val leaderBoardData = IIITData( Name = name, Points = points)
                            if(i < 3 ){
                                top3List.add(leaderBoardData)
                            }
                            else{
                                remainingList.add(leaderBoardData)
                            }
                        }
                        top3iiitslist3.addAll(top3List.map { IIITData(it.Name, it.Points) })
                        userListNo3.addAll(remainingList)
                        useAdapter.notifyDataSetChanged()
                        if (top3iiitslist3.isNotEmpty()) {
                            binding.first.text = top3iiitslist3[0].Name
                            binding.firstScore.text = top3iiitslist3[0].Points.toString()
                            Glide.with(requireContext())
                                .load(logo[top3iiitslist3[0].Name])
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(binding.firImg)
                        }
                        if (top3iiitslist3.size > 1) {
                            binding.second.text = top3iiitslist3[1].Name
                            binding.secondScore.text = top3iiitslist3[1].Points.toString()
                            Glide.with(requireContext())
                                .load(logo[top3iiitslist3[1].Name])
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(binding.secImg)
                        }
                        if (top3iiitslist3.size > 2) {
                            binding.third.text = top3iiitslist3[2].Name
                            binding.thirdScore.text = top3iiitslist3[2].Points.toString()
                            Glide.with(requireContext())
                                .load(logo[top3iiitslist3[2].Name])
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .into(binding.thiImg)
                        }
                        binding.resLot.visibility = View.INVISIBLE
                        binding.leaderRv.visibility = View.VISIBLE
                        binding.normal.visibility = View.VISIBLE
                        binding.error.visibility = View.INVISIBLE
                        binding.refresh.isRefreshing = false
                        Log.d("LeaderBoard_Stud", "Data fetched successfully: $dataArray")
                    } catch (e: JSONException) {
                        handleNetworkError()
                        Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
                        Log.e("LeaderBoard_Stud", "Error parsing JSON", e)
                    }
                },
                { error ->
                    handleNetworkError()
                    Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
                    Log.e("LeaderBoard_Stud", "Error parsing JSON",error)
                }
            )
            requestQueue.add(jsonObjectRequest)
    }
    private fun handleNetworkError() {
        binding.normal.visibility = View.INVISIBLE
        binding.error.visibility = View.VISIBLE
        binding.refresh.isRefreshing = false
        binding.loadBtn.setOnClickListener {
            fetchFromURl()
        }
    }
}
