package com.lx.server.walletapi.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lx.server.bean.ResultTO;
import com.lx.server.pojo.WalletAsset;
import com.lx.server.service.PopularAssetService;
import com.lx.server.service.WalletAssetService;
import com.lx.server.service.WalletService;
import com.lx.server.utils.Tools;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("api/wallet/asset")
@Api(tags = {"Address for assets in wallet"})
public class AssetController extends AbstractController{
	
	@Autowired
	private WalletAssetService walletAssetService;
	
	@Autowired
	private WalletService walletService ;
	
	@Autowired
	private PopularAssetService popularAssetService;
	
	@SuppressWarnings("serial")
	@GetMapping("list")
	@ApiOperation("Get assets list")
	public ResultTO getAddressList(String address) {
		List<WalletAsset> list = this.walletAssetService.selectObjectList(new HashMap<String,Object>() {{
			put("userId", getUserId());
			put("address", address);
		}});
		return ResultTO.newSuccessResult(list);
	}
	
	@SuppressWarnings({ "serial", "unchecked" })
	@PostMapping("setAssetVisible")
	@ApiOperation("Set visibility of an asset: if an asset can be displayed on screen")
	public ResultTO setAssetVisible(String address,Long assetId, Boolean visible) throws NumberFormatException, Exception {
		Assert.isTrue(Tools.checkStringExist(address), "address is null");
		Assert.isTrue(assetId!=null, "assetId is wrong");
		Assert.notNull(visible, "visible is null");
		
		int count = walletAssetService.pageCount(new HashMap<String,Object>() {{
			put("assetId", assetId);
			put("address", address);
		}});
		
		if (count==0) {
			Map<String, Object> node;
			if (assetId>0) {
				node = (Map<String, Object>) walletService.getOmniProperty(Long.parseLong(assetId.toString()));
			}else {
				node = new HashMap<>();
				node.put("name", "BTC");
			}
			WalletAsset asset = new WalletAsset();
			asset.setUserId(getUserId());
			asset.setVisible(visible);
			asset.setAddress(address);
			asset.setAssetName(node.get("name")!=null?node.get("name").toString():"");
			asset.setAssetType((byte) (assetId==0?0:1));
			asset.setAssetId(assetId);
			asset.setCreateTime(new Date());
			if (walletAssetService.insert(asset)>0) {
				return ResultTO.newSuccessResult("success");
			}
		}else {
			if (walletAssetService.update(new HashMap<String,Object>() {{
				put("n_assetId", assetId);
				put("n_address", address);
				put("visible", visible);
			}})>0) {
				return ResultTO.newSuccessResult("success");
			}
		}
		return ResultTO.newFailResult("fail");
	}
	
	@GetMapping("getPopularAssetList")
	@ApiOperation("Get assets list for promotion")
	public ResultTO getPopularAssetList() {
		return ResultTO.newSuccessResult(popularAssetService.selectObjectList(null));
	}
	
	
	
}
