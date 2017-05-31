function bestSatisfactionTest()
% Create a plot with the best Test values achieved

cd('../General');
% Initialization
clear ; close all; clc
X = load('satisfaction.txt');
% Max element of each GA Satisfaction row
XX = max(X(X(:,1)==1,3:end)');
plot([1:1:size(XX')], XX)
hold on;
xlabel('statistics gathered')
ylabel('best satisfaction achieved')
title('Best satisfaction value achieved in the Test Module','FontSize',12)
handle=gcf;
saveas(handle, ['Best_test', 'eps'],'epsc')
hold off;
cd('../Functions');
end

