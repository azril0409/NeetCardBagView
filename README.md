# NeetCardBagView
<img src="https://github.com/azril0409/NeetCardBagView/blob/d0b97bc5f7105968d3194ea90f5deaa7ee20c669/sample.gif" alt="sample.gif" width="300px">
#Compatibilty
API 11+
#Usage

##in layout xml:
```
<library.neetoffice.com.neetcardbagview.CardBagView
        android:id="@+id/cardBagView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:paddingBottom="@dimen/activity_vertical_margin"
        android:paddingLeft="@dimen/activity_horizontal_margin"
        android:paddingRight="@dimen/activity_horizontal_margin"
        android:paddingTop="@dimen/activity_vertical_margin"
        app:library_cardbag_topPercent="30%" />
        
```
##in java code:
create Adapter extend CardBagAdapter or CardBagTextAdapter
```
cardBagView = (CardBagView) findViewById(R.id.cardBagView);
cardBagView.setAdapter(new Adapter(this));
```

#License
```
Copyright 2015 TU TSUNG-TSE
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
