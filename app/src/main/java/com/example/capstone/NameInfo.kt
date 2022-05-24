package com.example.capstone

import java.io.Serializable

data class NameInfo(var id : String? = null, var name : String, val ingredient : String , val url : String, val tag : String) : Serializable
//uid =해당 레시피의 고유 코드
/*Serializable(직렬화)
자바 시스템 내부에서 사용하는 객체를 외부의 자바 시스템에서도 사용할 수 있도록 byte형태로 데이터를 전환 시키는 기술
안드로이드 상에선 직렬화를 이용해 액티비티간 또는 서비스간 클래스타입의 데이터를 주고 받는 용도로 주로 쓰임

그래서 데이터 클래스에 Serializable을 사용해 SearchPageActivity에서 SearchActivity로 데이터를 옮김
 */

/*else if (nameList!![i].ingredient.contains(searchTextList[i])) { //<-searchTextList에서 전부 부합되는 레시피를 찾아서 나오도록 손봐야함
                    searchList?.add(NameInfo(nameList!![i].id, nameList!![i].name, nameList!![i].ingredient, nameList!![i].url))
                    binding.SearchPageRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding.SearchPageRecyclerView.setHasFixedSize(true)
                    binding.SearchPageRecyclerView.adapter = SearchAdapter(searchList)
                }
       Log.d(TAG, "$$$$$$$$$$$$$$searchTextList")
*/