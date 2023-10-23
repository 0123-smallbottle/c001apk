package com.example.c001apk.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.c001apk.databinding.FragmentHomeBinding
import com.example.c001apk.ui.activity.search.SearchActivity
import com.example.c001apk.ui.fragment.home.app.AppListFragment
import com.example.c001apk.ui.fragment.home.feed.HomeFeedFragment
import com.example.c001apk.ui.fragment.home.follow.FollowFragment
import com.example.c001apk.ui.fragment.home.ranking.HomeRankingFragment
import com.example.c001apk.ui.fragment.home.topic.TopicFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by lazy { ViewModelProvider(this)[HomeViewModel::class.java] }
    private val tabList = arrayOf("关注", "应用", "头条", "热榜", "话题")
    private var fragmentList = ArrayList<Fragment>()

    companion object {
        var current = 1
    }

    init {
        fragmentList.run {
            add(FollowFragment())
            add(AppListFragment())
            add(HomeFeedFragment())
            add(HomeRankingFragment())
            add(TopicFragment())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: Tab?) {
                current = tab!!.position
            }

            override fun onTabUnselected(tab: Tab?) {
            }

            override fun onTabReselected(tab: Tab?) {
            }

        })

        initView()
        initMenu()

    }

    private fun initMenu() {
        binding.search.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
        }
    }

    /*private fun initMenu() {
        binding.toolBar.inflateMenu(R.menu.home_feed_menu)
        binding.toolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search -> startActivity(Intent(activity, SearchActivity::class.java))
            }
            return@setOnMenuItemClickListener true
        }
    }*/

    private fun initView() {
        binding.viewPager.offscreenPageLimit = tabList.size
        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int) = fragmentList[position]
            override fun getItemCount() = tabList.size
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabList[position]
        }.attach()
        if (viewModel.isInitial) {
            binding.viewPager.setCurrentItem(2, false)
            viewModel.isInitial = false
        }
    }


}