package mimosale.com.notification;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import mimosale.com.R;
import mimosale.com.home.fragments.AllTypeFragment;
import mimosale.com.home.fragments.ProductsFragment;
import mimosale.com.home.fragments.ShopSaleFragment;

public class CouponPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    Context context;
    public CouponPagerAdapter(FragmentManager fm, int NumOfTabs, Context context) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.context=context;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyCouponFragment();
            case 1:
                return new ClaimedCouponFragment();
             default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        String title = null;
        if (position == 0) {
            title = context.getResources().getString(R.string.my_coupons);
        } else if (position == 1) {
            title =context.getResources().getString(R.string.claimed_coupons);
        }
        return title;
    }
}
