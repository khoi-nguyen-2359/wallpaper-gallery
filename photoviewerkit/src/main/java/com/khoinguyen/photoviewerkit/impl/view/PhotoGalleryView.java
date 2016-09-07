package com.khoinguyen.photoviewerkit.impl.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.samples.zoomable.DefaultZoomableController;
import com.facebook.samples.zoomable.ZoomableController;
import com.facebook.samples.zoomable.ZoomableDraweeView;
import com.khoinguyen.apptemplate.eventbus.IEventBus;
import com.khoinguyen.apptemplate.listing.adapter.IListingAdapter;
import com.khoinguyen.apptemplate.listing.adapter.PagerListingAdapter;
import com.khoinguyen.apptemplate.listing.item.BaseViewHolder;
import com.khoinguyen.apptemplate.listing.item.ListingItemType;
import com.khoinguyen.photoviewerkit.R;
import com.khoinguyen.photoviewerkit.impl.anim.ZoomToAnimation;
import com.khoinguyen.photoviewerkit.impl.data.PhotoDisplayInfo;
import com.khoinguyen.photoviewerkit.impl.data.SharedData;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryDragStart;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoGalleryPhotoSelect;
import com.khoinguyen.photoviewerkit.impl.event.OnPhotoRecenterAnimationUpdate;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoGalleryView;
import com.khoinguyen.photoviewerkit.interfaces.IPhotoViewerKitWidget;
import com.khoinguyen.photoviewerkit.util.AdapterPhotoFinder;
import com.khoinguyen.photoviewerkit.util.ViewDragHelper;
import com.khoinguyen.util.log.L;

/**
 * Created by khoinguyen on 4/11/16.
 */
public class PhotoGalleryView extends ViewPager implements IPhotoGalleryView<SharedData> {
	private static final int PAGING_OFFSET = 3;
	private static final int DEF_OFFSCREEN_PAGE = 1;
	private static final long DURATION_DRAG_CANCEL = 200;

	protected L log = L.get("PhotoGalleryView");

	protected AdapterPhotoFinder adapterPhotoFinder;

	protected IEventBus eventBus;

	protected PhotoGalleryPagerAdapter adapterPhotoGallery;

	protected IPhotoViewerKitWidget<SharedData> photoKitWidget;

	protected SharedData sharedData;
	protected ViewDragHelper viewDragHelper;
	protected float endDragMinDistance;
	private boolean pagingNextHasFired = false;
	private ViewDragHelper.DragEventListener dragEventListener = new ViewDragHelper.DragEventListener() {
		@Override
		public void onDragStart() {
			eventBus.post(new OnPhotoGalleryDragStart());
		}

		@Override
		public void onDragEnd(float totalDistanceX, float totalDistanceY) {
			double dragDistance = Math.hypot(totalDistanceX, totalDistanceY);
			if (dragDistance > endDragMinDistance) {
				RectF fullRect = getCurrentRect();
				photoKitWidget.returnToListing(fullRect);
			} else {
				new ZoomToAnimation()
					.rects(getCurrentRect(), new RectF(0, 0, getWidth(), getHeight()))
					.duration(DURATION_DRAG_CANCEL)
					.target(PhotoGalleryView.this)
					.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							eventBus.post(new OnPhotoRecenterAnimationUpdate(animation.getAnimatedFraction()));
						}
					})
					.run();
			}
		}

		@Override
		public void onDragUpdate(float translationX, float translationY) {
			translate(getTranslationX() + translationX, getTranslationY() + translationY);
		}
	};
	private ViewPager.SimpleOnPageChangeListener internalOnPageChangeListener = new SimpleOnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			super.onPageSelected(position);

			onPagerPageSelected(position);
		}
	};

	public PhotoGalleryView(Context context) {
		super(context);
		init();
	}

	public PhotoGalleryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		setOffscreenPageLimit(DEF_OFFSCREEN_PAGE);

		Resources resources = getResources();
		endDragMinDistance = resources.getDimensionPixelSize(R.dimen.photo_gallery_end_drag_distance);

		viewDragHelper = new ViewDragHelper(getContext());
		viewDragHelper.setDragEventListener(dragEventListener);

		addOnPageChangeListener(internalOnPageChangeListener);
		adapterPhotoGallery = new PhotoGalleryPagerAdapter();
		setAdapter(adapterPhotoGallery);

		int pageMargin = resources.getDimensionPixelSize(R.dimen.photo_gallery_page_margin);
		setPageMargin(pageMargin);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (super.onInterceptTouchEvent(ev)) {
			viewDragHelper.reset();
			return true;
		}

		return viewDragHelper.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return viewDragHelper.onTouchEvent(ev) || super.onTouchEvent(ev);
	}

	private RectF getCurrentRect() {
		return new RectF(getX(), getY(), getX() + getWidth(), getY() + getHeight());
	}

	@Override
	public void translate(float x, float y) {
		setTranslationX(x);
		setTranslationY(y);
	}

	public void setPhotoAdapter(IListingAdapter photoAdapter) {
		adapterPhotoGallery.setListingViewAdapter(photoAdapter);
		if (adapterPhotoFinder == null || adapterPhotoFinder.getAdapter() != photoAdapter) {
			adapterPhotoFinder = new AdapterPhotoFinder(photoAdapter);
		}
	}

	@Override
	public void attach(IPhotoViewerKitWidget<SharedData> widget) throws UnsupportedOperationException {
		sharedData = widget.getSharedData();
		eventBus = widget.getEventBus();
		photoKitWidget = widget;
	}

	private void onPagerPageSelected(int position) {
		updateCurrentSelectedItemInfo(position);
		checkPagingNext(position);
	}

	private void checkPagingNext(int position) {
		if (!pagingNextHasFired && position >= adapterPhotoGallery.getCount() - PAGING_OFFSET) {
			photoKitWidget.onPagingNext(this);
			pagingNextHasFired = true;
		}
	}

	private void updateCurrentSelectedItemInfo(int position) {
		PhotoDisplayInfo photoDisplayInfo = adapterPhotoFinder.getPhoto(position);
		if (photoDisplayInfo == null) {
			return;
		}

		sharedData.activePhoto(photoDisplayInfo);

		eventBus.post(new OnPhotoGalleryPhotoSelect(position, photoDisplayInfo));
	}

	@Override
	public void enablePaging() {
		pagingNextHasFired = false;
	}

	/**
	 * EVENT HANDLERS
	 */

	@Override
	public void setCurrentPhoto(String photoId) {
		int itemIndex = adapterPhotoFinder.indexOf(photoId);
		if (itemIndex == AdapterPhotoFinder.NO_POSITION) {
			return;
		}

		setCurrentItem(itemIndex, false);
	}

	@Override
	public void setCurrentItem(int item, boolean smoothScroll) {
		if (item == getCurrentItem()) {
			// This case the current item doesnt change, so onPageSelected wont fire
			onPagerPageSelected(item);
		}

		super.setCurrentItem(item, smoothScroll);
	}

	@Override
	public void setCurrentPhoto(int itemIndex) {
		PhotoDisplayInfo photo = adapterPhotoFinder.getPhoto(itemIndex);
		if (photo == null) {
			return;
		}

		setCurrentItem(itemIndex, false);
	}

	@Override
	public void show() {
		setVisibility(VISIBLE);
	}

	@Override
	public void zoomPrimaryItem(Matrix transformMatrix) {
		ZoomableDraweeView primaryItemView = (ZoomableDraweeView) findViewWithTag(adapterPhotoGallery.primaryItemAdapterPosition);
		if (primaryItemView == null) {
			return;
		}

		ZoomableController zoomableController = primaryItemView.getZoomableController();
		if (zoomableController instanceof DefaultZoomableController) {
			DefaultZoomableController animatedZoomableController = (DefaultZoomableController) zoomableController;
			animatedZoomableController.setTransform(transformMatrix);
		}
	}

	@Override
	public void hide() {
		setVisibility(GONE);
	}

	public static class PhotoItemType extends ListingItemType<BaseViewHolder> {
		private LayoutInflater layoutInflater;

		public PhotoItemType(int viewType) {
			super(viewType);
		}

		@Override
		public View createView(ViewGroup container) {
			if (layoutInflater == null) {
				layoutInflater = LayoutInflater.from(container.getContext());
			}

			ZoomableDraweeView itemView = (ZoomableDraweeView) layoutInflater.inflate(R.layout.photokit_photo_gallery_pager_item, container, false);
			itemView.getHierarchy().setFadeDuration(0);
			itemView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);

			return itemView;
		}

		@Override
		public BaseViewHolder createViewHolder(View view) {
			return new PhotoGalleryItemViewHolder((ZoomableDraweeView) view);
		}
	}

	public static class PhotoGalleryItemViewHolder extends BaseViewHolder<PhotoDisplayInfo> {
		protected ZoomableDraweeView draweeView;

		private PhotoGalleryItemViewHolder(ZoomableDraweeView itemView) {
			super(itemView);
			this.draweeView = itemView;
			draweeView.setIsLongpressEnabled(false);
		}

		@Override
		public void bind(PhotoDisplayInfo data) {
			DraweeController controller = Fresco.newDraweeControllerBuilder()
				.setRetainImageOnFailure(true)
				.setLowResImageRequest(ImageRequest.fromUri(data.getLowResUri()))
				.setImageRequest(ImageRequest.fromUri(data.getHighResUri()))
				.setOldController(draweeView.getController())
//          .setCallerContext(this)
				.build();
			draweeView.setController(controller);
		}
	}

	/**
	 * END - EVENT HANDLERS
	 */

	private static class PhotoGalleryPagerAdapter extends PagerListingAdapter {
		int primaryItemAdapterPosition;

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);

			if (object instanceof View) {
				View itemView = (View) object;
				// // TODO: 7/12/16 what if this tag is used by other source
				itemView.setTag(position);
				primaryItemAdapterPosition = position;
			}
		}

		@Override
		public float getPageWidth(int position) {
			return 1;
		}
	}

}
