package com.devfn.seller.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class OrdersViewPageAdapter extends FragmentPagerAdapter
{
    private final List<Fragment> fragmentList=new ArrayList<>();
    private final List<String> fragment_list_title=new ArrayList<>();

    public OrdersViewPageAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        return fragmentList.get(position);
    }

    @Override
    public int getCount()
    {
        return fragment_list_title.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragment_list_title.get(position);
    }

    public void AddFragment(Fragment fragment, String title)
    {
        fragmentList.add(fragment);
        fragment_list_title.add(title);
    }
}
