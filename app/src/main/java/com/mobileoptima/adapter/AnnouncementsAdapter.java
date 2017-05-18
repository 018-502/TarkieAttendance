package com.mobileoptima.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.codepan.cache.TypefaceCache;
import com.codepan.utils.CodePanUtils;
import com.codepan.widget.CodePanLabel;
import com.mobileoptima.model.AnnouncementObj;
import com.mobileoptima.tarkieattendance.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

public class AnnouncementsAdapter extends ArrayAdapter<AnnouncementObj> {

	private DisplayImageOptions options;
	private ArrayList<AnnouncementObj> items;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;

	public AnnouncementsAdapter(Context context, ArrayList<AnnouncementObj> items) {
		super(context, 0, items);
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.items = items;
		this.imageLoader = ImageLoader.getInstance();
		if(!imageLoader.isInited()) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.color.gray_qua)
				.showImageForEmptyUri(R.color.gray_qua)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(30))
				.build();
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		final AnnouncementObj obj = items.get(position);
		if(obj != null) {
			if(view == null) {
				view = inflater.inflate(R.layout.announcements_list_row, parent, false);
				holder = new ViewHolder();
				holder.ivPhotoAnnouncements = (ImageView) view.findViewById(R.id.ivPhotoAnnouncements);
				holder.tvSubjectAnnouncements = (CodePanLabel) view.findViewById(R.id.tvSubjectAnnouncements);
				holder.tvAnnouncedByAnnouncements = (CodePanLabel) view.findViewById(R.id.tvAnnouncedByAnnouncements);
				holder.tvAnnouncedTimeAnnouncements = (CodePanLabel) view.findViewById(R.id.tvAnnouncedTimeAnnouncements);
				holder.tvAnnouncedDateAnnouncements = (CodePanLabel) view.findViewById(R.id.tvAnnouncedDateAnnouncements);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			if(obj.subject != null) {
				holder.tvSubjectAnnouncements.setText(obj.subject);
				String font;
				if(obj.isSeen) {
					font = getContext().getString(R.string.proxima_nova_mid);
				}
				else {
					font = getContext().getString(R.string.proxima_nova_bold);
				}
				Typeface typeface = TypefaceCache.get(getContext().getAssets(), font);
				holder.tvSubjectAnnouncements.setTypeface(typeface);
			}
			if(obj.announcedBy != null) {
				holder.tvAnnouncedByAnnouncements.setText(obj.announcedBy);
				imageLoader.displayImage(obj.announcedByImageURL, holder.ivPhotoAnnouncements, options);
			}
			if(obj.announcedTime != null) {
				String time = CodePanUtils.formatTime(obj.announcedTime);
				holder.tvAnnouncedTimeAnnouncements.setText(" | ".concat(time));
			}
			if(obj.announcedDate != null) {
				String date = CodePanUtils.getCalendarDate(obj.announcedDate, true, true);
				holder.tvAnnouncedDateAnnouncements.setText(" | ".concat(date));
			}
		}
		return view;
	}

	private class ViewHolder {
		private ImageView ivPhotoAnnouncements;
		private CodePanLabel tvSubjectAnnouncements;
		private CodePanLabel tvAnnouncedByAnnouncements;
		private CodePanLabel tvAnnouncedTimeAnnouncements;
		private CodePanLabel tvAnnouncedDateAnnouncements;
	}
}
