package cn.worldwalker.game.wyqp.mj.test;

import junit.framework.TestCase;

public class Test //extends TestCase
{
	public Test()
	{
		TableMgr.getInstance().load();
	}
	
	public void test()
	{
		
	}
	public static void main(String[] args) {
		Test t = new Test();
		t.testOne();
	}
	public void testOne()
	{
		int guiIndex = 33;
		int[] cards = { 
			0, 0, 0, 1, 1, 1, 0, 0, 0, /* 万 */ 
			1, 1, 1, 0, 0, 0, 0, 0, 0, /* 筒 */
			2, 0, 0, 0, 0, 0, 0, 0, 0, /* 条 */
			0, 0, 0, 0, 0, 0, 0 };//字
//
//		System.out.println("测试1种,癞子:" + guiIndex);
//		Program.print_cards(cards);
//		System.out.println(Hulib.getInstance().get_hu_info(cards, 34, guiIndex));
		guiIndex = 34;
		cards = new int[]{ 
			0, 0, 0, 1, 1, 1, 0, 0, 0, /* 万 */ 
			1, 1, 1, 0, 0, 0, 0, 0, 0, /* 筒 */
			3, 0, 0, 0, 0, 3, 0, 0, 0, /* 条 */
			2, 0, 0, 0, 0, 0, 0 };//字

		System.out.println("测试1种,laizi:" + guiIndex);
		Program.print_cards(cards);
		System.out.println(Hulib.getInstance().get_hu_info(cards, 34, guiIndex));
		
		guiIndex = 18;
		cards = new int[]{ 
			1, 0, 1, 0, 1, 0, 1, 0, 0, /* 万 */ 
			1, 1, 1, 0, 0, 0, 3, 0, 0, /* 筒 */
			2, 0, 0, 0, 0, 0, 0, 0, 0, /* 条 */
			2, 0, 0, 0, 0, 0, 0 };//字

		System.out.println("测试1种,laizi:" + guiIndex);
		Program.print_cards(cards);
		System.out.println(Hulib.getInstance().get_hu_info(cards, 34, guiIndex));
		
		
		guiIndex = 18;
		cards = new int[]{ 
			1, 0, 1, 0, 1, 0, 1, 0, 0, /* 万 */ 
			1, 1, 1, 0, 0, 0, 3, 0, 0, /* 筒 */
			2, 0, 0, 0, 0, 0, 0, 0, 0, /* 条 */
			2, 0, 0, 0, 0, 0, 0 };//字

		System.out.println("测试1种,laizi:" + guiIndex);
		Program.print_cards(cards);
		System.out.println(Hulib.getInstance().get_hu_info(cards, 34, guiIndex));
	}
}
