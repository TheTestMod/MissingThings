package thetestmod.mthings.storage;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thetestmod.mthings.MissingThings;
import thetestmod.mthings.utils.GuiHandlerRegistry;

public class StorageBox extends BlockContainer implements ITileEntityProvider
{   
	public static final int GUI_ID = 0;
	public StorageBox(String name, Material material, SoundType sound)
	{
		super(material);
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(MissingThings.CREATIVE_TAB);
        this.setHardness(2.0F);
        this.setHarvestLevel("pickaxe", 0);
	}

  /**
   * Create the Tile Entity for this block.
   * If your block doesn't extend BlockContainer, use createTileEntity(World worldIn, IBlockState state) instead
   * @param worldIn
   * @param meta
   * @return
   */

  @Override
  public TileEntity createNewTileEntity(World worldIn, int meta) {
    return new StorageBoxTileEntity();
  }

  // not needed if your block implements ITileEntityProvider (in this case implemented by BlockContainer), but it
  //  doesn't hurt to include it anyway...
	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	// Called when the block is right clicked
	// In this block it is used to open the blocks gui when right clicked by a player
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand,
																	EnumFacing side, float hitX, float hitY, float hitZ) {

		if (worldIn.isRemote) return true;

		playerIn.openGui(MissingThings.instance, StorageBoxGuiHandler.getGuiID(), worldIn, pos.getX(), pos.getY(), pos.getZ());
		
		return true;
	}
	
	// This is where you can do something when the block is broken. In this case drop the inventory's contents
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {

		IInventory inventory = worldIn.getTileEntity(pos) instanceof IInventory ? (IInventory)worldIn.getTileEntity(pos) : null;

		if (inventory != null){
			// For each slot in the inventory
			for (int i = 0; i < inventory.getSizeInventory(); i++){
				// If the slot is not empty
				if (!inventory.getStackInSlot(i).isEmpty())  // isEmpty
				{
					// Create a new entity item with the item stack in the slot
					EntityItem item = new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, inventory.getStackInSlot(i));

					// Apply some random motion to the item
					float multiplier = 0.1f;
					float motionX = worldIn.rand.nextFloat() - 0.5f;
					float motionY = worldIn.rand.nextFloat() - 0.5f;
					float motionZ = worldIn.rand.nextFloat() - 0.5f;

					item.motionX = motionX * multiplier;
					item.motionY = motionY * multiplier;
					item.motionZ = motionZ * multiplier;

					// Spawn the item in the world
					worldIn.spawnEntity(item);
				}
			}

			// Clear the inventory so nothing else (such as another mod) can do anything with the items
			inventory.clear();
		}

		// Super MUST be called last because it removes the tile entity
		super.breakBlock(worldIn, pos, state);
	}

	//---------------------------------------------------------

	@Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
        tooltip.add(I18n.format("item.storage_box.tooltip"));
    }
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.SOLID;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return true;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return true;
	}

  // render using a BakedModel (mbe30_inventory_basic.json --> mbe30_inventory_basic_model.json)
  // required because the default (super method) is INVISIBLE for BlockContainers.
  @Override
  public EnumBlockRenderType getRenderType(IBlockState iBlockState) {
    return EnumBlockRenderType.MODEL;
  }
}
