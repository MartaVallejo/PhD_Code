function satisfactionTable50()
%Create a table with the satisfaction achieved by GA and RAN. The data is
%discretised each 50 ticks horizontal

cd('../General');
% Initialization
clear ; close all; clc
X = load('../General/satisfaction.txt');
A = grpstats(X, [X(:,1)]);
xVal = [mean(reshape(A(1,3:end),50,12),1);
     mean(reshape(A(2,3:end),50,12),1);
     mean(reshape(A(3,3:end),50,12),1)];
%Create an empty table
t = uitable; 
%data = [xVal(1,:) ; xVal(2,:) ; (xVal(2,:) * 100)./xVal(1,:)];
data = [ xVal(2,:); xVal(1,:) ; xVal(3,:)];
set(t,'Data', data');
set(t,'Position',[4 191 348 227]);
set(t,'ColumnName',{'Random', 'GA', '% Difference'});
set(t,'Columnformat',{'bank', 'bank', 'bank'});
set(t,'ColumnWidth',{100});
set(t,'RowName',[50:50:600]);
set(t,'FontSize', 9);
saveas(t, ['tab1', 'eps'],'epsc');
cd('../Functions');
end

